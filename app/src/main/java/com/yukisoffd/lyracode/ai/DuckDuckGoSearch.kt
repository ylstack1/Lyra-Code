package com.yukisoffd.lyracode.ai

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Lightweight DuckDuckGo Lite search scraper (borrowed from Agora).
 * Scrapes https://lite.duckduckgo.com/lite/ — zero-config, no API key required.
 * Regex-only HTML parsing; decodes DDG redirect URLs; deduplicates by URL.
 */
object DuckDuckGoSearch {
    data class WebResult(val title: String, val url: String, val snippet: String)

    sealed class Response {
        data class Success(val results: List<WebResult>) : Response()
        data class Error(val type: String, val message: String) : Response()
    }

    private const val BASE_URL = "https://lite.duckduckgo.com/lite/"
    private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36"
    private const val PAGE_SIZE = 10
    private const val MAX_PAGES = 5

    private val LINK_TEXT_REGEX = Regex("""<a\s[^>]*class=['"]result-link['"][^>]*>([\s\S]*?)</a>""")
    private val HREF_REGEX = Regex("""href=['"]([^'"]+)['"]""")
    private val SNIPPET_REGEX = Regex("""class=['"]result-snippet['"][^>]*>([\s\S]*?)</td>""")
    private val TAG_REGEX = Regex("""<[^>]+>""")

    private fun client(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    fun search(query: String, maxResults: Int = 6): Response {
        val client = client()
        val results = LinkedHashMap<String, WebResult>()
        var vqd = ""
        var offset = 0
        try {
            for (page in 0 until MAX_PAGES) {
                val form = okhttp3.FormBody.Builder().add("q", query)
                if (page > 0 && vqd.isNotBlank()) {
                    form.add("vqd", vqd).add("s", offset.toString()).add("o", "json").add("kl", "wt-wt")
                } else {
                    form.add("kl", "wt-wt")
                }
                val request = Request.Builder()
                    .url(BASE_URL)
                    .header("User-Agent", USER_AGENT)
                    .post(form.build())
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return if (results.isNotEmpty()) Response.Success(results.values.toList().take(maxResults))
                        else Response.Error("NETWORK_ERROR", "HTTP ${response.code}")
                    }
                    val body = response.body?.string().orEmpty()
                    if (body.contains("anomaly") || body.contains("challenge", ignoreCase = true)) {
                        return if (results.isNotEmpty()) Response.Success(results.values.toList().take(maxResults))
                        else Response.Error("CAPTCHA", "DuckDuckGo bot challenge; retry later")
                    }
                    if (vqd.isBlank()) {
                        vqd = Regex("""vqd=['"]?([\d-]+)['"]?""").find(body)?.groupValues?.getOrNull(1).orEmpty()
                    }
                    parsePage(body, results)
                }
                if (results.size >= maxResults) break
                offset += PAGE_SIZE
            }
        } catch (e: Exception) {
            return if (results.isNotEmpty()) Response.Success(results.values.toList().take(maxResults))
            else Response.Error("NETWORK_ERROR", e.message ?: "network failure")
        }
        return if (results.isEmpty()) Response.Error("NO_RESULTS", "no results for query")
        else Response.Success(results.values.toList().take(maxResults))
    }

    private fun parsePage(html: String, out: LinkedHashMap<String, WebResult>) {
        val links = LINK_TEXT_REGEX.findAll(html).toList()
        val snippets = SNIPPET_REGEX.findAll(html).toList()
        links.forEachIndexed { index, match ->
            val anchor = match.value
            val href = HREF_REGEX.find(anchor)?.groupValues?.get(1).orEmpty()
            val url = decodeDdgUrl(href)
            if (url.isNotBlank() && !out.containsKey(url)) {
                val title = stripTags(anchor)
                val snippet = snippets.getOrNull(index)?.let { stripTags(it.groupValues[1]) }.orEmpty()
                out[url] = WebResult(title = title, url = url, snippet = snippet)
            }
        }
    }

    private fun decodeDdgUrl(href: String): String {
        val raw = href.removePrefix("//duckduckgo.com/l/?uddg=")
        return if (raw != href) {
            val decoded = URLDecoder.decode(raw.substringBefore("&rut="), Charsets.UTF_8.name())
            decoded
        } else if (href.startsWith("http")) href else ""
    }

    private fun stripTags(html: String): String {
        var text = TAG_REGEX.replace(html, "")
        text = text.replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#x27;", "'")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
        return text.trim()
    }
}
