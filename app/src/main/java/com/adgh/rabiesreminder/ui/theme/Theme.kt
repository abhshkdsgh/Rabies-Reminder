package com.adgh.rabiesreminder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppColors {
    val Primary = Color(0xFF1565C0)       // deep blue
    val Accent = Color(0xFF00BFA5)        // teal
    val Background = Color(0xFFF6F8FA)    // very light gray
    val Surface = Color(0xFFFFFFFF)       // white
    val Success = Color(0xFF00BFA5)
    val Destructive = Color(0xFFD32F2F)
    val OnPrimary = Color.White
    val OnBackground = Color(0xFF111827)
    // Dark-mode fallbacks (used by theme below)
    val DarkPrimary = Color(0xFF90CAF9)
    val DarkAccent = Color(0xFF80E6C8)
    val DarkBackground = Color(0xFF0B1220)
    val DarkSurface = Color(0xFF071225)
    val DarkOnBackground = Color(0xFFE6EEF6)
    val DarkDestructive = Color(0xFFFF8A80)
}

fun contentColorFor(backgroundColor: Color): Color {
    return if (backgroundColor.luminance() > 0.5f) {
        Color.Black
    } else {
        Color.White
    }
}
private val LightColors = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    secondary = AppColors.Accent,
    background = AppColors.Background,
    surface = AppColors.Surface,
    error = AppColors.Destructive,
    onBackground = AppColors.OnBackground,
    onSurface = Color(0xFF0F1724),
    scrim = Color.LightGray
)

private val DarkColors = darkColorScheme(
    primary = AppColors.DarkPrimary,
    onPrimary = contentColorFor(AppColors.DarkPrimary),
    secondary = AppColors.DarkAccent,
    background = AppColors.DarkBackground,
    surface = AppColors.DarkSurface,
    error = AppColors.DarkDestructive,
    onBackground = AppColors.DarkOnBackground,
    onSurface = AppColors.DarkOnBackground,
    scrim = Color(0xFFFFFFBB)
)

private val AppTypography = Typography(
    displayLarge = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleLarge = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    titleSmall = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodyLarge = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodyMedium = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    bodySmall = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp)
)

@Composable
fun RabiesReminderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}