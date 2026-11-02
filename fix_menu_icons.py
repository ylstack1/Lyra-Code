path = "app/src/main/java/com/yukisoffd/lyracode/UiCommon.kt"
with open(path, "r") as f:
    content = f.read()

# Fix KimiMenuRow icon colors - use hardcoded colors based on icon type
# The first KimiMenuRow (String icon version)
old_menu1 = """        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, color = iconContent, style = MaterialTheme.typography.titleMedium)
        }"""

# This is complex. Let me instead just replace the tint values in the two KimiMenuRow functions
# to use IconColor.Primary instead of theme colors

# First KimiMenuRow - the icon tint
old_tint1 = """            tint = MaterialTheme.colorScheme.onSurfaceVariant,\n        )\n    }\n}\n\n@Composable\ninternal fun KimiMenuRow(\n    icon: ImageVector,"""

# Actually, let me just replace all MaterialTheme.colorScheme.onSurfaceVariant icon tints with IconColor.Secondary
# and MaterialTheme.colorScheme.primary icon tints with IconColor.Primary

# This is too complex for simple string replacement. Let me take a different approach
# and just update the most important icon usages in the main files.

print("Skipping complex menu icon replacement - will do targeted fixes instead")
