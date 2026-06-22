package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Centralized UI visual tokens for duplicate decoration values that already exist
 * in the accepted TITAN ORACLE UI. These tokens preserve the current rendered
 * appearance while removing scattered duplicate magic values from feature code.
 */
object TitanUiVisualTokens {
    val TerminalVoid = Color(0xFF02050D)
    val TerminalPanel = Color(0xFF050A13)
    val TerminalDeepPanel = Color(0xFF030712)
    val TerminalInnerPanel = Color(0xFF050812)
    val TerminalBluePanel = Color(0xFF0B1220)
    val TerminalCyanPanel = Color(0xFF03111B)
    val TerminalBlueBlack = Color(0xFF04111A)

    val TerminalBrightText = Color(0xFFF4F8FF)
    val TerminalSoftText = Color(0xFFD1D5DB)
    val AccuracyLossRed = Color(0xFFFF3F60)
    val DestructiveActionRed = Color(0xFFFF5252)
    val PurpleMetric = Color(0xFF9D65FF)
    val WarningOrange = Color(0xFFFF9F0A)

    val CompactShape = RoundedCornerShape(8.dp)
    val PanelShape = RoundedCornerShape(12.dp)
    val MediumPanelShape = RoundedCornerShape(10.dp)
    val SmallPillShape = RoundedCornerShape(6.dp)
    val MicroPillShape = RoundedCornerShape(4.dp)
}
