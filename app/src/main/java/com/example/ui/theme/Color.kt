package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.core.ui.theme.TitanOracleColors
import com.example.core.ui.theme.titanSetupStatusColor

// Legacy UI theme bridge. New shared primitives live in com.example.core.ui.theme.
val DarkBackground: Color = TitanOracleColors.DarkBackground
val DarkSurface: Color = TitanOracleColors.DarkSurface
val DarkSurfaceVariant: Color = TitanOracleColors.DarkSurfaceVariant
val CryptoGreen: Color = TitanOracleColors.SuccessLongGreen
val CryptoRed: Color = TitanOracleColors.DangerShortRed
val CryptoRedContainer: Color = TitanOracleColors.DangerRedContainer
val CryptoRedText: Color = TitanOracleColors.DangerRedText
val CryptoCyan: Color = TitanOracleColors.CyberCyan
val AccentGold: Color = TitanOracleColors.AccentGold
val TextPrimary: Color = TitanOracleColors.TextPrimary
val TextSecondary: Color = TitanOracleColors.TextSecondary
val TextMuted: Color = TitanOracleColors.TextMuted
val BorderColor: Color = TitanOracleColors.BorderColor

fun setupStatusColor(status: String?): Color = titanSetupStatusColor(status)
