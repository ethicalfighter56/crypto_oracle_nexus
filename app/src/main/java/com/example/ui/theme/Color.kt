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


// Project-wide scoring and execution-posture color tokens.
val TitanGreen: Color = CryptoGreen
val TitanGold: Color = TitanOracleColors.AccentGold
val TitanOrange: Color = TitanOracleColors.InstitutionalOrange
val TitanRed: Color = CryptoRedText
val TitanCyan: Color = CryptoCyan

fun titanPositiveScoreColor(score: Int): Color {
    val s = score.coerceIn(0, 100)
    return when {
        s >= 85 -> TitanGreen
        s >= 75 -> TitanGold
        s >= 65 -> TitanOrange
        else -> TitanRed
    }
}

fun titanRiskScoreLabel(score: Int): String {
    val s = score.coerceIn(0, 100)
    return when {
        s <= 15 -> "LOW"
        s <= 25 -> "MEDIUM"
        s <= 35 -> "HIGH"
        else -> "EXTREME"
    }
}

fun titanRiskScoreColor(score: Int): Color {
    val s = score.coerceIn(0, 100)
    return when {
        s <= 15 -> TitanGreen
        s <= 25 -> TitanGold
        s <= 35 -> TitanOrange
        else -> TitanRed
    }
}

fun titanRiskScoreFromPositiveScore(score: Int): Int = 100 - score.coerceIn(0, 100)

fun titanRiskScoreLabelFromPositiveScore(score: Int): String =
    titanRiskScoreLabel(titanRiskScoreFromPositiveScore(score))

fun titanRiskScoreColorFromPositiveScore(score: Int): Color =
    titanRiskScoreColor(titanRiskScoreFromPositiveScore(score))

fun titanRiskScoreColorFromLabel(label: String?): Color {
    val normalized = label.orEmpty().uppercase()
    return when {
        normalized.contains("LOW") -> TitanGreen
        normalized.contains("MEDIUM") -> TitanGold
        normalized.contains("HIGH") -> TitanOrange
        normalized.contains("EXTREME") || normalized.contains("CRITICAL") || normalized.contains("INVALID") -> TitanRed
        else -> TextSecondary
    }
}

fun titanRiskProfileForPositiveScore(score: Int): String {
    val s = score.coerceIn(0, 100)
    return when {
        s >= 85 -> "AGGRESSIVE"
        s >= 75 -> "MODERATE"
        else -> "CONSERVATIVE"
    }
}

fun titanConsensusBiasForPositiveScore(score: Int): String =
    titanRiskProfileForPositiveScore(score)

fun titanRiskProfileColor(profile: String?): Color {
    val normalized = profile.orEmpty().uppercase()
    return when {
        normalized.contains("CONSERVATIVE") -> TitanCyan
        normalized.contains("MODERATE") || normalized.contains("BALANCED") -> TitanGreen
        normalized.contains("AGGRESSIVE") -> TitanGold
        else -> TitanCyan
    }
}

fun titanAllocationProfileColor(profile: String?): Color = titanRiskProfileColor(profile)

fun titanCqiClassification(score: Int): String {
    val s = score.coerceIn(0, 100)
    return when {
        s >= 90 -> "INSTITUTIONAL GRADE"
        s >= 85 -> "HIGH CONFIDENCE"
        s >= 75 -> "MODERATE CONFIDENCE"
        s >= 65 -> "CAUTION"
        else -> "LOW CONFIDENCE"
    }
}

fun titanExecutionReadinessColor(status: String?): Color {
    val normalized = status.orEmpty().uppercase()
    return when {
        normalized.contains("OPTIMAL") -> TitanGreen
        normalized.contains("ACCEPTABLE") -> TitanGold
        normalized.contains("DEGRADED") -> TitanOrange
        normalized.contains("POOR") || normalized.contains("INVALID") -> TitanRed
        else -> TextSecondary
    }
}

fun titanMarketRegimeColor(regime: String?): Color {
    val normalized = regime.orEmpty().uppercase()
    return when {
        normalized.contains("BULL") || normalized.contains("ACCUMULATION") -> TitanGreen
        normalized.contains("BEAR") || normalized.contains("BREAKDOWN") || normalized.contains("CRASH") -> TitanRed
        normalized.contains("VOLATILITY") || normalized.contains("DISTRIBUTION") -> TitanOrange
        normalized.contains("SIDEWAYS") || normalized.contains("CONSOLIDATION") -> TitanGold
        else -> TitanCyan
    }
}
