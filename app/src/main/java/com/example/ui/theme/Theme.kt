package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PremiumGold,
    secondary = PaleGold,
    tertiary = TextGoldHighlight,
    background = DeepBlack,
    surface = CharcoalDark,
    onPrimary = Color(0xFF0F0F11),
    onSecondary = Color(0xFF0F0F11),
    onTertiary = Color(0xFF0F0F11),
    onBackground = TextLightOffWhite,
    onSurface = TextLightOffWhite,
    surfaceVariant = WarmGrey,
    onSurfaceVariant = TextLightOffWhite,
    outline = BorderGoldMuted
)

private val LightColorScheme = lightColorScheme(
    primary = PremiumGold,
    secondary = MetallicAmber,
    tertiary = DarkGoldAccent,
    background = Color(0xFFFCFAF5),
    surface = Color(0xFFF4EFE0),
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onTertiary = TextLightOffWhite,
    onBackground = DeepBlack,
    onSurface = DeepBlack,
    surfaceVariant = Color(0xFFECE4D0),
    onSurfaceVariant = DeepBlack,
    outline = BorderGoldMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to true for the requested Premium Black & Gold vibe!
    dynamicColor: Boolean = false, // Keep disabled to preserve our custom luxury branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
