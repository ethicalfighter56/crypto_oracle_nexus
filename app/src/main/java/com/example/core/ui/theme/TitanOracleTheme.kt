package com.example.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TitanOracleDarkColorScheme = darkColorScheme(
    primary = TitanCyberCyan,
    onPrimary = TitanDarkBackground,
    secondary = TitanAccentGold,
    onSecondary = TitanDarkBackground,
    tertiary = TitanSuccessLongGreen,
    onTertiary = TitanDarkBackground,
    background = TitanDarkBackground,
    onBackground = TitanTextPrimary,
    surface = TitanDarkSurface,
    onSurface = TitanTextPrimary,
    surfaceVariant = TitanDarkSurfaceVariant,
    onSurfaceVariant = TitanTextSecondary,
    error = TitanDangerShortRed,
    onError = TitanTextPrimary,
    outline = TitanBorderColor
)

@Composable
fun TitanOracleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TitanOracleDarkColorScheme,
        typography = TitanOracleTypography,
        shapes = TitanOracleShapes,
        content = content
    )
}
