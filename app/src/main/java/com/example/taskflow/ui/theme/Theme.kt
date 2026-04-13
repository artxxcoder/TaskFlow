package com.example.taskflow.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Global app settings (written by CustomizeScreen, read everywhere) ──
object AppSettings {
    var isDarkMode by mutableStateOf(true)
    var accentColor by mutableStateOf(Color(0xFF6C63FF))
}

object TaskFlowColors {
    // Backgrounds
    val BgDeep             = Color(0xFF0A0C14)
    val BgSurface          = Color(0xFF111420)
    val BgCard             = Color(0xFF181D2E)
    val BgElevated         = Color(0xFF1F2640)

    // Light mode backgrounds
    val LightBgDeep        = Color(0xFFF5F6FA)
    val LightBgSurface     = Color(0xFFFFFFFF)
    val LightBgCard        = Color(0xFFFFFFFF)
    val LightBgElevated    = Color(0xFFEEEFF5)

    // Accent — reads from AppSettings so it updates live
    val AccentPrimary get()      = AppSettings.accentColor
    val AccentPrimaryLight get() = AppSettings.accentColor.copy(alpha = 0.75f)
    val AccentPrimaryAlpha get() = AppSettings.accentColor.copy(alpha = 0.2f)

    // Accent Cyan (fixed)
    val AccentCyan         = Color(0xFF00D4FF)

    // Status colors
    val StatusSuccess      = Color(0xFF00E5A0)
    val StatusWarning      = Color(0xFFFFB347)
    val StatusDanger       = Color(0xFFFF5F7E)

    // Text — switches based on mode
    val TextPrimary get()    = if (AppSettings.isDarkMode) Color(0xFFFFFFFF) else Color(0xFF0A0C14)
    val TextSecondary get()  = if (AppSettings.isDarkMode) Color(0xFFA0AABF) else Color(0xFF4A5270)
    val TextMuted get()      = if (AppSettings.isDarkMode) Color(0xFF596073) else Color(0xFF8A93AB)

    // Borders
    val BorderSubtle get()   = if (AppSettings.isDarkMode) Color(0xFF1E2438) else Color(0xFFDDDFEA)

    // Dynamic backgrounds
    val Background get()     = if (AppSettings.isDarkMode) BgDeep        else LightBgDeep
    val Surface get()        = if (AppSettings.isDarkMode) BgSurface     else LightBgSurface
    val Card get()           = if (AppSettings.isDarkMode) BgCard        else LightBgCard
    val Elevated get()       = if (AppSettings.isDarkMode) BgElevated    else LightBgElevated
}

val TaskFlowTypography = Typography(
    displayLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,     fontSize = 42.sp, lineHeight = 48.sp, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,     fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 16.sp),
    bodyLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 16.sp),
    bodyMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 14.sp),
    bodySmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 12.sp),
    labelSmall    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 11.sp, letterSpacing = 0.5.sp),
)

@Composable
fun TaskFlowTheme(content: @Composable () -> Unit) {
    val isDark = AppSettings.isDarkMode
    val accent = AppSettings.accentColor

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary          = accent,
            onPrimary        = Color.White,
            secondary        = TaskFlowColors.AccentCyan,
            background       = TaskFlowColors.BgDeep,
            onBackground     = Color.White,
            surface          = TaskFlowColors.BgSurface,
            onSurface        = Color.White,
            surfaceVariant   = TaskFlowColors.BgCard,
            onSurfaceVariant = Color(0xFFA0AABF),
            outline          = Color(0xFF1E2438),
            error            = TaskFlowColors.StatusDanger,
        )
    } else {
        lightColorScheme(
            primary          = accent,
            onPrimary        = Color.White,
            secondary        = TaskFlowColors.AccentCyan,
            background       = TaskFlowColors.LightBgDeep,
            onBackground     = Color(0xFF0A0C14),
            surface          = TaskFlowColors.LightBgSurface,
            onSurface        = Color(0xFF0A0C14),
            surfaceVariant   = TaskFlowColors.LightBgCard,
            onSurfaceVariant = Color(0xFF4A5270),
            outline          = Color(0xFFDDDFEA),
            error            = TaskFlowColors.StatusDanger,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = TaskFlowTypography,
        content     = content
    )
}