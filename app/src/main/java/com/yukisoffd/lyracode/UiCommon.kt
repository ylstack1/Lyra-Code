package com.yukisoffd.lyracode

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.provider.MediaStore
import android.util.Base64
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.MediaController
import android.widget.VideoView
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.Switch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.yukisoffd.lyracode.ai.ChatRecord
import com.yukisoffd.lyracode.ai.AiResponseCache
import com.yukisoffd.lyracode.ai.OpenAiAgent
import com.yukisoffd.lyracode.ai.TodoItem
import com.yukisoffd.lyracode.ai.WebViewWebAgent
import com.yukisoffd.lyracode.data.ApiProfile
import com.yukisoffd.lyracode.data.AppSettings
import com.yukisoffd.lyracode.data.AuditEntry
import com.yukisoffd.lyracode.data.AuditLogStore
import com.yukisoffd.lyracode.data.BackupManager
import com.yukisoffd.lyracode.data.BackupOptions
import com.yukisoffd.lyracode.data.Conversation
import com.yukisoffd.lyracode.data.ConversationStore
import com.yukisoffd.lyracode.data.McpServerConfig
import com.yukisoffd.lyracode.data.McpToolDefinition
import com.yukisoffd.lyracode.data.SkillPack
import com.yukisoffd.lyracode.data.SshServerConfig
import com.yukisoffd.lyracode.data.WebDavServerConfig
import com.yukisoffd.lyracode.mcp.McpClientManager
import com.yukisoffd.lyracode.ssh.SshExecutor
import com.yukisoffd.lyracode.termux.TermuxExecutor
import com.yukisoffd.lyracode.webdav.TransferProgress
import com.yukisoffd.lyracode.webdav.WebDavClient
import com.yukisoffd.lyracode.workspace.GlobalFileManager
import com.yukisoffd.lyracode.workspace.NativeFileManager
import com.yukisoffd.lyracode.workspace.UploadedFile
import com.yukisoffd.lyracode.workspace.UploadedFileManager
import com.yukisoffd.lyracode.workspace.WorkspaceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date
import java.util.Locale
import kotlin.math.min
import kotlin.math.max
import kotlin.math.abs
import android.graphics.Canvas as AndroidCanvas

internal val LocalCodeFontFamily = staticCompositionLocalOf<FontFamily> { FontFamily.Monospace }

@Composable
internal fun LyraCodeTheme(
    darkMode: Boolean,
    dynamicColor: Boolean,
    fontScale: Float,
    settings: AppSettings,
    settingsRevision: Int,
    dynamicColorRevision: Int,
    content: @Composable () -> Unit,
) {
    val selectedPreset = remember(settingsRevision, settings.selectedThemePreset) {
        findThemePreset(settings.selectedThemePreset) ?: defaultThemePreset()
    }
    val context = LocalContext.current
    val density = LocalDensity.current
    val textFontFamily = remember(settingsRevision, settings.textFontPath) {
        settings.textFontPath?.let { path ->
            runCatching { FontFamily(Typeface.createFromFile(path)) }.getOrNull()
        } ?: FontFamily.Default
    }
    val codeFontFamily = remember(settingsRevision, settings.codeFontPath) {
        settings.codeFontPath?.let { path ->
            runCatching { FontFamily(Typeface.createFromFile(path)) }.getOrNull()
        } ?: FontFamily.Monospace
    }
    val customBackground = remember(settingsRevision, settings.customThemeColorEnabled, settings.customThemeColor) {
        if (settings.customThemeColorEnabled) {
            runCatching { Color(android.graphics.Color.parseColor(settings.customThemeColor)) }.getOrNull()
        } else null
    }
    val darkSurface = customBackground?.let { lerp(it, Color.Black, 0.55f) } ?: ModernSurfaceDark
    val darkSurfaceVariant = customBackground?.let { lerp(it, Color.Black, 0.40f) } ?: ModernSurfaceElevatedDark
    val light = lightColorScheme(
        // Text stays white on dark themes; only bg and accent change per theme
        primary = if (settings.customThemeColorEnabled) ModernAccentLight else selectedPreset.accent,
        onPrimary = Color.White,
        primaryContainer = if (settings.customThemeColorEnabled) ModernAccentLight.copy(alpha = 0.20f) else selectedPreset.accentContainer,
        onPrimaryContainer = Color.White,
        secondary = if (settings.customThemeColorEnabled) ModernMutedLight else selectedPreset.muted,
        onSecondary = Color.White,
        background = customBackground ?: if (settings.customThemeColorEnabled) ModernBgLight else selectedPreset.background,
        surface = if (settings.customThemeColorEnabled) ModernSurfaceLight else selectedPreset.surface,
        surfaceVariant = Color(0xFFF5F5F4),
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFFB0B0B0),
        onBackground = Color.White,
        outline = if (settings.customThemeColorEnabled) ModernBorderLight else selectedPreset.border,
        outlineVariant = if (settings.customThemeColorEnabled) ModernBorderLight.copy(alpha = 0.5f) else selectedPreset.outlineVariant,
        surfaceTint = if (settings.customThemeColorEnabled) ModernAccentLight else selectedPreset.accent,
    )
    val dark = darkColorScheme(
        // Text stays white on dark themes; only bg and accent change per theme
        primary = if (settings.customThemeColorEnabled) ModernAccentDark else selectedPreset.accent,
        onPrimary = Color.White,
        primaryContainer = if (settings.customThemeColorEnabled) ModernAccentDark.copy(alpha = 0.25f) else selectedPreset.accentContainer,
        onPrimaryContainer = Color.White,
        secondary = if (settings.customThemeColorEnabled) ModernMutedDark else selectedPreset.muted,
        onSecondary = Color.White,
        background = customBackground ?: if (settings.customThemeColorEnabled) ModernBgDark else selectedPreset.background,
        surface = if (settings.customThemeColorEnabled) ModernSurfaceDark else selectedPreset.surface,
        surfaceVariant = if (settings.customThemeColorEnabled) ModernSurfaceElevatedDark else selectedPreset.surfaceElevated,
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFFB0B0B0),
        onBackground = Color.White,
        outline = if (settings.customThemeColorEnabled) ModernBorderDark else selectedPreset.border,
        outlineVariant = if (settings.customThemeColorEnabled) ModernBorderDark.copy(alpha = 0.4f) else selectedPreset.outlineVariant,
        surfaceTint = if (settings.customThemeColorEnabled) ModernAccentDark else selectedPreset.accent,
    )
    val baseScheme = if (darkMode) dark else light
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicScheme = remember(dynamicColorRevision, darkMode, context) {
            if (darkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        if (customBackground == null) {
            dynamicScheme
        } else baseScheme.copy(
            primary = dynamicScheme.primary,
            onPrimary = dynamicScheme.onPrimary,
            primaryContainer = dynamicScheme.primaryContainer,
            onPrimaryContainer = dynamicScheme.onPrimaryContainer,
            inversePrimary = dynamicScheme.inversePrimary,
            secondary = dynamicScheme.secondary,
            onSecondary = dynamicScheme.onSecondary,
            secondaryContainer = dynamicScheme.secondaryContainer,
            onSecondaryContainer = dynamicScheme.onSecondaryContainer,
            tertiary = dynamicScheme.tertiary,
            onTertiary = dynamicScheme.onTertiary,
            tertiaryContainer = dynamicScheme.tertiaryContainer,
            onTertiaryContainer = dynamicScheme.onTertiaryContainer,
            error = dynamicScheme.error,
            onError = dynamicScheme.onError,
            errorContainer = dynamicScheme.errorContainer,
            onErrorContainer = dynamicScheme.onErrorContainer,
            surfaceTint = dynamicScheme.primary,
        )
    } else {
        baseScheme
    }
    val defaults = Typography()
    val typography = defaults.copy(
        displayLarge = defaults.displayLarge.copy(fontFamily = textFontFamily, fontSize = 32.sp, fontWeight = FontWeight.SemiBold),
        displayMedium = defaults.displayMedium.copy(fontFamily = textFontFamily, fontSize = 28.sp, fontWeight = FontWeight.SemiBold),
        displaySmall = defaults.displaySmall.copy(fontFamily = textFontFamily, fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
        headlineLarge = defaults.headlineLarge.copy(fontFamily = textFontFamily, fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
        headlineMedium = defaults.headlineMedium.copy(fontFamily = textFontFamily, fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
        headlineSmall = defaults.headlineSmall.copy(fontFamily = textFontFamily, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
        titleLarge = defaults.titleLarge.copy(fontFamily = textFontFamily, fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
        titleMedium = defaults.titleMedium.copy(fontFamily = textFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Medium),
        titleSmall = defaults.titleSmall.copy(fontFamily = textFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Medium),
        bodyLarge = defaults.bodyLarge.copy(fontFamily = textFontFamily, fontSize = 16.sp),
        bodyMedium = defaults.bodyMedium.copy(fontFamily = textFontFamily, fontSize = 14.sp),
        bodySmall = defaults.bodySmall.copy(fontFamily = textFontFamily, fontSize = 12.sp),
        labelLarge = defaults.labelLarge.copy(fontFamily = textFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Medium),
        labelMedium = defaults.labelMedium.copy(fontFamily = textFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium),
        labelSmall = defaults.labelSmall.copy(fontFamily = textFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium),
    )
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, fontScale.coerceIn(AppSettings.MIN_FONT_SCALE, AppSettings.MAX_FONT_SCALE)),
        LocalCodeFontFamily provides codeFontFamily,
    ) {
        MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
    }
}
internal val ModernBgDark = Color(0xFF0A0A0A)
internal val ModernSurfaceDark = Color(0xFF141414)
internal val ModernSurfaceElevatedDark = Color(0xFF1C1C1C)
internal val ModernBorderDark = Color(0xFF2A2A2A)
internal val ModernTextDark = Color(0xFFEAEAEA)
internal val ModernMutedDark = Color(0xFF8E8E8E)
internal val ModernAccentDark = Color(0xFF6366F1)

internal val ModernBgLight = Color(0xFFFAFAF9)
internal val ModernSurfaceLight = Color(0xFFFFFFFF)
internal val ModernBorderLight = Color(0xFFE7E5E4)
internal val ModernTextLight = Color(0xFF18181B)
internal val ModernMutedLight = Color(0xFF71717A)
internal val ModernAccentLight = Color(0xFF4F46E5)

internal val ModernCardShape = RoundedCornerShape(12.dp)
internal val ModernPillShape = RoundedCornerShape(999.dp)
internal val ModernShape = RoundedCornerShape(8.dp)

// ============================================================================
// Hardcoded Icon Colors by Type (do not change with theme)
// ============================================================================

internal object IconColor {
    // Action colors
    val Save = Color(0xFF4CAF50)      // Green - save, confirm, check
    val Delete = Color(0xFFF44336)    // Red - delete, remove, error
    val Edit = Color(0xFF2196F3)      // Blue - edit, modify, change
    val Info = Color(0xFF9E9E9E)      // Gray - info, details, secondary
    val Add = Color(0xFF4CAF50)       // Green - add, create, new
    val Settings = Color(0xFF9E9E9E)  // Gray - settings, config
    val Copy = Color(0xFF9E9E9E)      // Gray - copy, duplicate
    val Share = Color(0xFF9E9E9E)     // Gray - share, export
    val Download = Color(0xFF2196F3)  // Blue - download, import
    val Upload = Color(0xFF2196F3)    // Blue - upload, send
    val Search = Color(0xFF9E9E9E)    // Gray - search, find
    val Menu = Color(0xFF9E9E9E)      // Gray - menu, navigation
    val Back = Color(0xFF9E9E9E)      // Gray - back, close, cancel
    val Warning = Color(0xFFFF9800)   // Amber - warning, alert
    val Success = Color(0xFF4CAF50)   // Green - success, done
    val Error = Color(0xFFF44336)     // Red - error, failure
    val Primary = Color(0xFF6366F1)   // Indigo - primary action
    val Secondary = Color(0xFF9E9E9E) // Gray - secondary action
}

// ============================================================================
// End Icon Colors
// ============================================================================



// ============================================================================
// Theme Preset System - Built-in dark themes (Vercel, Obsidian, GitHub, etc.)
// ============================================================================

internal data class ThemePreset(
    val id: String,
    val name: String,
    val description: String,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val text: Color,
    val onBackground: Color,
    val onSurface: Color,
    val muted: Color,
    val accent: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val outlineVariant: Color,
)

internal val ThemePresetGitHubDark = ThemePreset(
    id = "github_dark",
    name = "GitHub Dark",
    description = "Deep dark with subtle blue accents",
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    surfaceElevated = Color(0xFF21262D),
    border = Color(0xFF30363D),
    text = Color(0xFFC9D1D9),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    muted = Color(0xFF8B949E),
    accent = Color(0xFF58A6FF),
    onAccent = Color(0xFF000000),
    accentContainer = Color(0xFF1F6FEB).copy(alpha = 0.15f),
    outlineVariant = Color(0xFF30363D).copy(alpha = 0.7f),
)

internal val ThemePresetVercelDark = ThemePreset(
    id = "vercel_dark",
    name = "Vercel Dark",
    description = "Near-black canvas with indigo accents",
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF141414),
    surfaceElevated = Color(0xFF1C1C1C),
    border = Color(0xFF2A2A2A),
    text = Color(0xFFEAEAEA),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    muted = Color(0xFF8E8E8E),
    accent = Color(0xFF0000FF),
    onAccent = Color(0xFFFFFFFF),
    accentContainer = Color(0xFF0000FF).copy(alpha = 0.12f),
    outlineVariant = Color(0xFF2A2A2A).copy(alpha = 0.6f),
)

internal val ThemePresetObsidian = ThemePreset(
    id = "obsidian",
    name = "Obsidian",
    description = "True black with high-contrast text",
    background = Color(0xFF0B0E11),
    surface = Color(0xFF13161F),
    surfaceElevated = Color(0xFF1A1D26),
    border = Color(0xFF2A2E3A),
    text = Color(0xFFE0E0E0),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    muted = Color(0xFFA0A0A0),
    accent = Color(0xFF7C3AED),
    onAccent = Color(0xFFFFFFFF),
    accentContainer = Color(0xFF7C3AED).copy(alpha = 0.15f),
    outlineVariant = Color(0xFF2A2E3A).copy(alpha = 0.6f),
)

internal val ThemePresetGoldenDragon = ThemePreset(
    id = "golden_dragon",
    name = "Golden Dragon Dark",
    description = "Deep black with gold accents",
    background = Color(0xFF050505),
    surface = Color(0xFF0A0A0A),
    surfaceElevated = Color(0xFF121212),
    border = Color(0xFF1F1F1F),
    text = Color(0xFFFFFFFF),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    muted = Color(0xFFD4AF37),
    accent = Color(0xFFFFD700),
    onAccent = Color(0xFF000000),
    accentContainer = Color(0xFFFFD700).copy(alpha = 0.15f),
    outlineVariant = Color(0xFF1F1F1F).copy(alpha = 0.6f),
)

internal val ThemePresetMatte = ThemePreset(
    id = "matte",
    name = "Matte Dark",
    description = "Soft matte gray with muted tones",
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceElevated = Color(0xFF2A2A2A),
    border = Color(0xFF3A3A3A),
    text = Color(0xFFE0E0E0),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    muted = Color(0xFFB0B0B0),
    accent = Color(0xFFBB86FC),
    onAccent = Color(0xFF000000),
    accentContainer = Color(0xFFBB86FC).copy(alpha = 0.15f),
    outlineVariant = Color(0xFF3A3A3A).copy(alpha = 0.6f),
)

internal val AllThemePresets = listOf(
    ThemePresetGitHubDark,
    ThemePresetVercelDark,
    ThemePresetObsidian,
    ThemePresetGoldenDragon,
    ThemePresetMatte,
)

internal fun findThemePreset(id: String): ThemePreset? =
    AllThemePresets.firstOrNull { it.id == id }

internal fun defaultThemePreset(): ThemePreset = ThemePresetVercelDark

// ============================================================================
// End Theme Preset System
// ============================================================================



@Composable
internal fun ModernCardBox(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ModernCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}

@Composable
internal fun ModernDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
    )
}

@Composable
internal fun KimiSectionLabel(text: String) {
    Text(
        text,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 2.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.titleSmall,
    )
}

@Composable
private fun kimiMenuAccent(seed: String): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when ((seed.hashCode() and Int.MAX_VALUE) % 3) {
        0 -> scheme.primaryContainer.copy(alpha = 0.72f) to scheme.onPrimaryContainer
        1 -> scheme.secondaryContainer.copy(alpha = 0.72f) to scheme.onSecondaryContainer
        else -> scheme.tertiaryContainer.copy(alpha = 0.72f) to scheme.onTertiaryContainer
    }
}

@Composable
internal fun KimiMenuRow(
    icon: String,
    title: String,
    value: String = "",
    onClick: () -> Unit = {},
) {
    val (iconContainer, iconContent) = kimiMenuAccent(title)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(ModernCardShape)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(IconColor.Primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, color = IconColor.Primary, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (value.isNotBlank()) {
                Text(
                    value,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = IconColor.Secondary,
        )
    }
}

@Composable
internal fun KimiMenuRow(
    icon: ImageVector,
    title: String,
    value: String = "",
    enabled: Boolean = true,
    onDisabledClick: (() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    val (iconContainer, iconContent) = if (enabled) kimiMenuAccent(title) else
        MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        Modifier
            .fillMaxWidth()
            .clip(ModernCardShape)
            .clickable(enabled = enabled || onDisabledClick != null) { if (enabled) onClick() else onDisabledClick?.invoke() }
            .then(if (enabled) Modifier else Modifier.graphicsLayer { alpha = 0.38f })
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(23.dp),
                tint = IconColor.Primary,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (value.isNotBlank()) {
                Text(
                    value,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = IconColor.Secondary,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun KimiMenuRowWithLongClick(
    icon: ImageVector,
    title: String,
    value: String = "",
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val (iconContainer, iconContent) = kimiMenuAccent(title)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(ModernCardShape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(23.dp),
                tint = IconColor.Primary,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (value.isNotBlank()) {
                Text(
                    value,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = IconColor.Secondary,
        )
    }
}

@Composable
internal fun PlusBadgeIcon(
    baseIcon: @Composable () -> Unit,
) {
    Box(Modifier.size(30.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) {
            baseIcon()
        }
        Text(
            "+",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.background, CircleShape)
                .padding(horizontal = 1.dp),
            color = IconColor.Primary,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
internal fun KimiChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Text(
        text,
        modifier = modifier
            .clip(ModernPillShape)
            .clickable(onClick = onClick)
            .background(IconColor.Primary.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        color = IconColor.Primary,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal enum class NoticeType {
    SUCCESS, WARNING, ERROR, INFO
}

internal fun TransientNotice(
    message: String,
    type: NoticeType = NoticeType.SUCCESS,
    modifier: Modifier = Modifier,
    durationMillis: Long = 2400L,
    onDismiss: () -> Unit,
) {
    var visible by remember(message) { mutableStateOf(message.isNotBlank()) }
    LaunchedEffect(message) {
        if (message.isBlank()) return@LaunchedEffect
        visible = true
        kotlinx.coroutines.delay(durationMillis)
        visible = false
        kotlinx.coroutines.delay(220)
        onDismiss()
    }
    val (bgColor, icon, iconRes) = when (type) {
        NoticeType.SUCCESS -> Color(0xFF4CAF50) to Icons.Default.CheckCircle to null
        NoticeType.WARNING -> Color(0xFFFF9800) to Icons.Default.Warning to null
        NoticeType.ERROR -> Color(0xFFF44336) to Icons.Default.Error to null
        NoticeType.INFO -> Color(0xFF2196F3) to Icons.Default.Info to null
    }
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = visible && message.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Surface(
                shape = ModernPillShape,
                color = bgColor,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
            ) {
                Row(
                    Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                    Text(
                        message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ConfirmDeleteDialog(
    title: String,
    message: String,
    targetName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var remaining by remember(title, targetName) { mutableIntStateOf(3) }
    LaunchedEffect(title, targetName) {
        remaining = 3
        repeat(3) {
            kotlinx.coroutines.delay(1000)
            remaining--
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Text(targetName, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    if (remaining > 0) uiText(R.string.ui_please_wait) + remaining + uiText(R.string.ui_seconds_before_confirming_deletion) else uiText(R.string.ui_this_cannot_be_restored_automatically_please_confirm),
                    color = if (remaining > 0) ModernMutedDark else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(
                enabled = remaining <= 0,
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) {
                Text(if (remaining > 0) uiText(R.string.file_action_delete) + " ($remaining)" else uiText(R.string.file_delete_title))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(uiText(R.string.action_cancel)) }
        },
    )
}

