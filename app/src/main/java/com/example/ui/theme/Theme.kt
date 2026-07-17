package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OceanTealDark,
    secondary = SandGoldDark,
    tertiary = ForestGreenDark,
    background = CoastBgDark,
    surface = CoastSurfaceDark,
    onPrimary = Color(0xFF00363D),
    onSecondary = Color(0xFF4D2600),
    onTertiary = Color(0xFF003914),
    onBackground = Color(0xFFE0F1F4),
    onSurface = Color(0xFFE0F1F4),
    surfaceVariant = Color(0xFF1B363E),
    onSurfaceVariant = Color(0xFFB0BEC5)
)

private val LightColorScheme = lightColorScheme(
    primary = OceanTeal,
    secondary = SandGold,
    tertiary = ForestGreen,
    background = CoastBgLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F1E24),
    onSurface = Color(0xFF0F1E24),
    surfaceVariant = Color(0xFFE0ECEF),
    onSurfaceVariant = Color(0xFF3F4D52)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We can allow override if required, defaulting to system dark theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
