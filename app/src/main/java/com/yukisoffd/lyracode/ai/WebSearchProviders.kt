package com.yukisoffd.lyracode.ai

import com.yukisoffd.lyracode.data.AppSettings
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Multi-provider web search (borrowed from Agora).
 * Defaults to DuckDuckGo (zero-config, no API key). SearXNG also needs no key.
 * Brave / Tavily / Serper / Kagi use the configured API key.
 */
object WebSearchProviders {

    data class Config(
        val provider: String,
        val apiKey: String,
        val baseUrl: String,
    )

    fun fromSettings(settings: AppSettings): Config = Config(
        provider = settings.webSearchProvider,
        apiKey = settings.webSearchApiKey,
        baseUrl = settings.webSearchApiBaseUrl,
    )

    private fun client(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    fun search(config: Config, query: String, limit: Int): String {
        val capped = limit.coerceIn(1, 10)
        return try {
            when (config.provider) {
                AppSettings.WEB_SEARCH_SEARXNG -> searxng(config, query, capped)
                AppSettings.WEB_SEARCH_BRAVE -> brave(config, query, capped)
                AppSettings.WEB_SEARCH_TAVILY -> tavily(config, query, capped)
                AppSettings.WEB_SEARCH_SERPER -> serper(config, query, capped)
                AppSettings.WEB_SEARCH_KAGI -> kagi(config, query, capped)
                else -> duckduckgo(query, capped)
            }
        } catch (e: Exception) {
            errorJson(query, e.message ?: "search failed")
        }
    }

    private fun ok(query: String, results: List<Triple<String, String, String>>): String {
        if (results.isEmpty()) return errorJson(query, "no_results")
        val array = JSONArray()
        results.forEach { (title, url, snippet) ->
            array.put(JSONObject().put("title", title).put("url", url).put("description", snippet))
        }
        return JSONObject().put("type", "web_search").put("query", query).put("results", array).toString()
    }

    private fun errorJson(query: String, message: String): String =
        JSONObject().put("type", "web_search").put("query", query).put("error", message).toString()

    // ---- DuckDuckGo (default, no key) ----
    private fun duckduckgo(query: String, limit: Int): String = when (val r = DuckDuckGoSearch.search(query, limit)) {
        is DuckDuckGoSearch.Response.Success ->
            ok(query, r.results.map { Triple(it.title, it.url, it.snippet) })
        is DuckDuckGoSearch.Response.Error -> errorJson(query, "${r.type}: ${r.message}")
    }

    // ---- SearXNG (no key; public instance by default) ----
    private fun searxng(config: Config, query: String, limit: Int): String {
        val base = config.baseUrl.ifBlank { "https://searx.be" }.trimEnd('/')
        val url = "$base/search?q=${URLEncoder.encode(query, "UTF-8")}&format=json"
        val request = Request.Builder().url(url).header("User-Agent", "Mozilla/5.0 (compatible; LyraCode/1.0)").build()
        client().newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) return errorJson(query, "HTTP ${response.code}")
            val root = runCatching { JSONObject(body) }.getOrNull() ?: return errorJson(query, "invalid JSON")
            val results = mutableListOf<Triple<String, String, String>>()
            val arr = root.optJSONArray("results") ?: return errorJson(query, "no_results")
            for (i in 0 until arr.length().coerceAtMost(limit)) {
                val item = arr.optJSONObject(i) ?: continue
                val u = item.optString("url")
                if (u.isBlank()) continue
                results.add(Triple(item.optString("title"), u, item.optString("content")))
            }
            return ok(query, results)
        }
    }

    // ---- Brave Search (key) ----
    private fun brave(config: Config, query: String, limit: Int): String {
        if (config.apiKey.isBlank()) return errorJson(query, "missing_api_key")
        val url = "https://api.search.brave.com/res/v1/web/search?q=${URLEncoder.encode(query, "UTF-8")}&count=$limit"
        val request = Request.Builder().url(url)
            .header("X-Subscription-Token", config.apiKey)
            .header("Accept", "application/json")
            .build()
        client().newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) return errorJson(query, "HTTP ${response.code}")
            val root = runCatching { JSONObject(body) }.getOrNull() ?: return errorJson(query, "invalid JSON")
            val web = root.optJSONObject("web") ?: return errorJson(query, "no_results")
            val arr = web.optJSONArray("results") ?: return errorJson(query, "no_results")
            val results = mutableListOf<Triple<String, String, String>>()
            for (i in 0 until arr.length().coerceAtMost(limit)) {
                val item = arr.optJSONObject(i) ?: continue
                val u = item.optString("url")
                if (u.isBlank()) continue
                results.add(Triple(item.optString("title"), u, item.optString("description")))
            }
            return ok(query, results)
        }
    }

    // ---- Tavily (key) ----
    private fun tavily(config: Config, query: String, limit: Int): String {
        if (config.apiKey.isBlank()) return errorJson(query, "missing_api_key")
        val payload = JSONObject().put("query", query).put("max_results", limit).put("api_key", config.apiKey)
        val request = Request.Builder().url("https://api.tavily.com/search")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()
        client().newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) return errorJson(query, "HTTP ${response.code}")
            val root = runCatching { JSONObject(body) }.getOrNull() ?: return errorJson(query, "invalid JSON")
            val arr = root.optJSONArray("results") ?: return errorJson(query, "no_results")
            val results = mutableListOf<Triple<String, String, String>>()
            for (i in 0 until arr.length().coerceAtMost(limit)) {
                val item = arr.optJSONObject(i) ?: continue
                val u = item.optString("url")
                if (u.isBlank()) continue
                results.add(Triple(item.optString("title"), u, item.optString("content")))
            }
            return ok(query, results)
        }
    }

    // ---- Serper (key) ----
    private fun serper(config: Config, query: String, limit: Int): String {
        if (config.apiKey.isBlank()) return errorJson(query, "missing_api_key")
        val payload = JSONObject().put("q", query).put("num", limit)
        val request = Request.Builder().url("https://google.serper.dev/search")
            .header("X-API-KEY", config.apiKey)
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()
        client().newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) return errorJson(query, "HTTP ${response.code}")
            val root = runCatching { JSONObject(body) }.getOrNull() ?: return errorJson(query, "invalid JSON")
            val arr = root.optJSONArray("organic") ?: return errorJson(query, "no_results")
            val results = mutableListOf<Triple<String, String, String>>()
            for (i in 0 until arr.length().coerceAtMost(limit)) {
                val item = arr.optJSONObject(i) ?: continue
                val u = item.optString("link")
                if (u.isBlank()) continue
                results.add(Triple(item.optString("title"), u, item.optString("snippet")))
            }
            return ok(query, results)
        }
    }

    // ---- Kagi (key) ----
    private fun kagi(config: Config, query: String, limit: Int): String {
        if (config.apiKey.isBlank()) return errorJson(query, "missing_api_key")
        val payload = JSONObject().put("query", query).put("workflow", "search").put("limit", limit)
        val request = Request.Builder().url("https://kagi.com/api/v0/search")
            .header("Authorization", "Bot ${config.apiKey}")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()
        client().newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) return errorJson(query, "HTTP ${response.code}")
            val root = runCatching { JSONObject(body) }.getOrNull() ?: return errorJson(query, "invalid JSON")
            val data = root.optJSONObject("data") ?: return errorJson(query, "no_results")
            val arr = data.optJSONArray("search_results") ?: return errorJson(query, "no_results")
            val results = mutableListOf<Triple<String, String, String>>()
            for (i in 0 until arr.length().coerceAtMost(limit)) {
                val item = arr.optJSONObject(i) ?: continue
                val u = item.optString("url")
                if (u.isBlank()) continue
                results.add(Triple(item.optString("title"), u, item.optString("snippet")))
            }
            return ok(query, results)
        }
    }
}
