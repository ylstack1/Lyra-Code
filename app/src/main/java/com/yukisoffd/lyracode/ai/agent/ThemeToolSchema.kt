package com.yukisoffd.lyracode.ai.agent

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.yukisoffd.lyracode.AllThemePresets
import com.yukisoffd.lyracode.data.AppSettings
import com.yukisoffd.lyracode.findThemePreset
import com.yukisoffd.lyracode.defaultThemePreset
import org.json.JSONArray
import org.json.JSONObject

internal fun getThemeInfoToolDefinition(): JSONObject = JSONObject().put("type", "function").put("function", JSONObject()
    .put("name", "get_theme_info")
    .put("description", "Get current Lyra Code theme information including active preset, colors, and custom theme settings.")
    .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
        .put("properties", JSONObject())))

internal fun listThemePresetsToolDefinition(): JSONObject = JSONObject().put("type", "function").put("function", JSONObject()
    .put("name", "list_theme_presets")
    .put("description", "List all available built-in theme presets with their names, descriptions, and color values.")
    .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
        .put("properties", JSONObject())))

internal fun setThemePresetToolDefinition(): JSONObject = JSONObject().put("type", "function").put("function", JSONObject()
    .put("name", "set_theme_preset")
    .put("description", "Set the active theme preset for Lyra Code. Changes the app background, accent, and surface colors.")
    .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
        .put("required", JSONArray(listOf("preset_id")))
        .put("properties", JSONObject()
            .put("preset_id", JSONObject().put("type", "string")
                .put("description", "Theme preset ID: github_dark, vercel_dark, obsidian, golden_dragon, or matte")))))

internal fun getCustomThemesToolDefinition(): JSONObject = JSONObject().put("type", "function").put("function", JSONObject()
    .put("name", "get_custom_themes")
    .put("description", "List all saved custom themes with their names, descriptions, and index numbers.")
    .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
        .put("properties", JSONObject())))

internal fun applyCustomThemeToolDefinition(): JSONObject = JSONObject().put("type", "function").put("function", JSONObject()
    .put("name", "apply_custom_theme")
    .put("description", "Apply a custom theme from JSON. The JSON must contain schema, name, preset_id, and color values (background, surface, accent, text, etc.).")
    .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
        .put("required", JSONArray(listOf("theme_json")))
        .put("properties", JSONObject()
            .put("theme_json", JSONObject().put("type", "string")
                .put("description", "Complete theme JSON with schema, name, description, preset_id, background, surface, accent, text, and other color values")))))

internal fun exportThemeJsonToolDefinition(): JSONObject = JSONObject().put("type", "function").put("function", JSONObject()
    .put("name", "export_theme_json")
    .put("description", "Export the current Lyra Code theme as JSON. Returns the complete theme JSON that can be saved or shared.")
    .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
        .put("properties", JSONObject())))

internal fun themeToolResult(
    settings: AppSettings,
    callName: String,
    callArgs: JSONObject,
): String {
    return when (callName) {
        "get_theme_info" -> {
            val preset = findThemePreset(settings.selectedThemePreset) ?: defaultThemePreset()
            JSONObject()
                .put("schema", "lyra_theme_info_v1")
                .put("active_preset", settings.selectedThemePreset)
                .put("preset_name", preset.name)
                .put("preset_description", preset.description)
                .put("custom_enabled", settings.customThemeColorEnabled)
                .put("custom_color", settings.customThemeColor)
                .put("custom_theme_count", settings.customThemeCount())
                .toString()
        }
        "list_theme_presets" -> {
            val array = JSONArray()
            AllThemePresets.forEach { preset ->
                array.put(JSONObject()
                    .put("id", preset.id)
                    .put("name", preset.name)
                    .put("description", preset.description)
                    .put("background", preset.background.toArgb())
                    .put("surface", preset.surface.toArgb())
                    .put("accent", preset.accent.toArgb())
                    .put("text", preset.text.toArgb())
                    .put("border", preset.border.toArgb()))
            }
            JSONObject()
                .put("schema", "lyra_theme_presets_v1")
                .put("presets", array)
                .toString()
        }
        "set_theme_preset" -> {
            val presetId = callArgs.getString("preset_id")
            if (findThemePreset(presetId) == null) {
                "ERROR: Unknown theme preset ID: ${presetId}. Available: ${AllThemePresets.joinToString(",") { it.id }}"
            } else {
                settings.selectedThemePreset = presetId
                "Theme preset changed to: $presetId"
            }
        }
        "get_custom_themes" -> {
            val array = JSONArray()
            for (i in 0 until settings.customThemeCount()) {
                val json = settings.getCustomTheme(i) ?: ""
                val name = runCatching { JSONObject(json).optString("name", "Custom Theme ${i + 1}") }.getOrDefault("Custom Theme ${i + 1}")
                val desc = runCatching { JSONObject(json).optString("description", "") }.getOrDefault("")
                array.put(JSONObject()
                    .put("index", i)
                    .put("name", name)
                    .put("description", desc)
                    .put("json", json))
            }
            JSONObject()
                .put("schema", "lyra_custom_themes_v1")
                .put("themes", array)
                .toString()
        }
        "apply_custom_theme" -> {
            val themeJson = callArgs.getString("theme_json")
            val result = runCatching {
                val root = JSONObject(themeJson)
                val presetId = root.optString("preset_id", "vercel_dark")
                if (findThemePreset(presetId) != null) {
                    settings.selectedThemePreset = presetId
                }
                if (root.has("custom_enabled")) {
                    settings.customThemeColorEnabled = root.optBoolean("custom_enabled")
                }
                root.optString("custom_color").takeIf { it.isNotBlank() }?.let { settings.customThemeColor = it }
                val themeName = root.optString("name", "")
                if (themeName.isNotBlank()) {
                    settings.addCustomTheme(themeJson)
                }
                "Custom theme applied: $themeName"
            }
            result.getOrElse { "ERROR: ${it.message}" }
        }
        "export_theme_json" -> {
            val preset = findThemePreset(settings.selectedThemePreset) ?: defaultThemePreset()
            JSONObject()
                .put("schema", "lyra_theme_v1")
                .put("name", preset.name)
                .put("description", preset.description)
                .put("preset_id", settings.selectedThemePreset)
                .put("custom_enabled", settings.customThemeColorEnabled)
                .put("custom_color", settings.customThemeColor)
                .put("background", preset.background.toArgb())
                .put("surface", preset.surface.toArgb())
                .put("surface_elevated", preset.surfaceElevated.toArgb())
                .put("border", preset.border.toArgb())
                .put("text", preset.text.toArgb())
                .put("on_background", preset.onBackground.toArgb())
                .put("on_surface", preset.onSurface.toArgb())
                .put("muted", preset.muted.toArgb())
                .put("accent", preset.accent.toArgb())
                .put("on_accent", preset.onAccent.toArgb())
                .put("accent_container", preset.accentContainer.toArgb())
                .put("outline_variant", preset.outlineVariant.toArgb())
                .toString()
        }
        else -> "ERROR: Unknown theme tool: $callName"
    }
}
