# Fix 1: OpenAiAgent.kt - replace 'agent' with 'settings' in theme tool handlers
path = "app/src/main/java/com/yukisoffd/lyracode/ai/agent/OpenAiAgent.kt"
with open(path, "r") as f:
    content = f.read()

# The theme tool handlers reference 'agent' but should reference 'settings'
old = '''                "get_theme_info" -> ToolExecution(com.yukisoffd.lyracode.ai.agent.ThemeToolSchema.themeToolResult(settings, "get_theme_info", args))
                "list_theme_presets" -> ToolExecution(com.yukisoffd.lyracode.ai.agent.ThemeToolSchema.themeToolResult(settings, "list_theme_presets", args))
                "set_theme_preset" -> ToolExecution(com.yukisoffd.lyracode.ai.agent.ThemeToolSchema.themeToolResult(settings, "set_theme_preset", args))
                "get_custom_themes" -> ToolExecution(com.yukisoffd.lyracode.ai.agent.ThemeToolSchema.themeToolResult(settings, "get_custom_themes", args))
                "apply_custom_theme" -> ToolExecution(com.yukisoffd.lyracode.ai.agent.ThemeToolSchema.themeToolResult(settings, "apply_custom_theme", args))
                "export_theme_json" -> ToolExecution(com.yukisoffd.lyracode.ai.agent.ThemeToolSchema.themeToolResult(settings, "export_theme_json", args))'''

# Wait, the error says "Unresolved reference 'agent'" not 'ThemeToolSchema'
# Let me check what's actually in the file
print("Checking OpenAiAgent.kt theme handlers...")
import re
for line in content.split('\n'):
    if 'ThemeToolSchema' in line:
        print(f"  {line.strip()[:120]}")
        break

# Fix 2: SettingsScreens.kt - add Row import
path2 = "app/src/main/java/com/yukisoffd/lyracode/ui/settings/SettingsScreens.kt"
with open(path2, "r") as f:
    content2 = f.read()

if "import androidx.compose.foundation.layout.Row" not in content2:
    old_imp = "import androidx.compose.foundation.layout.Column"
    new_imp = "import androidx.compose.foundation.layout.Column\nimport androidx.compose.foundation.layout.Row"
    if old_imp in content2:
        content2 = content2.replace(old_imp, new_imp, 1)
        with open(path2, "w") as f:
            f.write(content2)
        print("Added Row import to SettingsScreens.kt")

print("Done")
