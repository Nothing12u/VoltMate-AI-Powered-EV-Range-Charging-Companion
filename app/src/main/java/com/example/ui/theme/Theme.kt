package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VoltMateDarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = DarkBg,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = ElectricLime,
    secondary = EmeraldGreen,
    onSecondary = DarkBg,
    secondaryContainer = EmeraldGreenDim,
    onSecondaryContainer = EmeraldGreen,
    tertiary = ElectricBlue,
    onTertiary = DarkBg,
    tertiaryContainer = ElectricBlueDim,
    onTertiaryContainer = ElectricBlue,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    error = DangerCoral,
    onError = DarkBg
)

private val VoltMateHighContrastColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = Color.Black,
    primaryContainer = Color.Black,
    onPrimaryContainer = ElectricLime,
    secondary = EmeraldGreen,
    onSecondary = Color.Black,
    tertiary = ElectricBlue,
    onTertiary = Color.Black,
    background = HighContrastBg,
    onBackground = HighContrastText,
    surface = HighContrastSurface,
    onSurface = HighContrastText,
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = Color.White,
    outline = HighContrastBorder,
    error = DangerCoral,
    onError = Color.Black
)

@Composable
fun VoltMateTheme(
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) {
        VoltMateHighContrastColorScheme
    } else {
        VoltMateDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
