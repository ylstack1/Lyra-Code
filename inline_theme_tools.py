path = "app/src/main/java/com/yukisoffd/lyracode/ai/agent/AgentToolSchemaFactory.kt"
with open(path, "r") as f:
    content = f.read()

# Remove the import
old_import = "import com.yukisoffd.lyracode.ai.agent.ThemeToolSchema\n"
content = content.replace(old_import, "")

# Replace the theme tool definitions with inline definitions
old_theme_tools = """        .put(ThemeToolSchema.getThemeInfoToolDefinition())
        .put(ThemeToolSchema.listThemePresetsToolDefinition())
        .put(ThemeToolSchema.setThemePresetToolDefinition())
        .put(ThemeToolSchema.getCustomThemesToolDefinition())
        .put(ThemeToolSchema.applyCustomThemeToolDefinition())
        .put(ThemeToolSchema.exportThemeJsonToolDefinition())"""

new_theme_tools = """        .put(JSONObject().put("type", "function").put("function", JSONObject()
            .put("name", "get_theme_info")
            .put("description", "Get current Lyra Code theme information including active preset, colors, and custom theme settings.")
            .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
                .put("properties", JSONObject()))))
        .put(JSONObject().put("type", "function").put("function", JSONObject()
            .put("name", "list_theme_presets")
            .put("description", "List all available built-in theme presets with their names, descriptions, and color values.")
            .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
                .put("properties", JSONObject()))))
        .put(JSONObject().put("type", "function").put("function", JSONObject()
            .put("name", "set_theme_preset")
            .put("description", "Set the active theme preset for Lyra Code. Changes the app background, accent, and surface colors.")
            .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
                .put("required", JSONArray(listOf("preset_id")))
                .put("properties", JSONObject()
                    .put("preset_id", JSONObject().put("type", "string")
                        .put("description", "Theme preset ID: github_dark, vercel_dark, obsidian, golden_dragon, or matte"))))))
        .put(JSONObject().put("type", "function").put("function", JSONObject()
            .put("name", "get_custom_themes")
            .put("description", "List all saved custom themes with their names, descriptions, and index numbers.")
            .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
                .put("properties", JSONObject()))))
        .put(JSONObject().put("type", "function").put("function", JSONObject()
            .put("name", "apply_custom_theme")
            .put("description", "Apply a custom theme from JSON. The JSON must contain schema, name, preset_id, and color values (background, surface, accent, text, etc.).")
            .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
                .put("required", JSONArray(listOf("theme_json")))
                .put("properties", JSONObject()
                    .put("theme_json", JSONObject().put("type", "string")
                        .put("description", "Complete theme JSON with schema, name, description, preset_id, background, surface, accent, text, and other color values"))))))
        .put(JSONObject().put("type", "function").put("function", JSONObject()
            .put("name", "export_theme_json")
            .put("description", "Export the current Lyra Code theme as JSON. Returns the complete theme JSON that can be saved or shared.")
            .put("parameters", JSONObject().put("type", "object").put("additionalProperties", false)
                .put("properties", JSONObject()))))

assert old_theme_tools in content, "Theme tools not found"
content = content.replace(old_theme_tools, new_theme_tools)

with open(path, "w") as f:
    f.write(content)

print("Inlined theme tools into AgentToolSchemaFactory.kt")
