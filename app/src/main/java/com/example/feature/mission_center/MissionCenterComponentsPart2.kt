package com.example.feature.mission_center

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CryptoViewModel
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

import com.example.ui.theme.DarkBackground

// Terminal Colors - Institutional Grade

// Extracted from MissionCenterScreen.kt to keep the public screen entry point compact.
@Composable
fun RunningMissionsContent(viewModel: CryptoViewModel, isBengali: Boolean) {
    val missions by viewModel.activeMissions.collectAsState()
    
    if (missions.isEmpty()) {
        EmptyMissionTerminal(isBengali)
    } else {
        val displayMissions = missions
            .mapIndexed { index, mission -> index to mission }
            .sortedWith(
                compareBy<Pair<Int, com.example.model.Mission>> { mcMissionDisplaySortRank(it.second, it.first).first }
                    .thenBy { mcMissionDisplaySortRank(it.second, it.first).second }
                    .thenBy { mcMissionDisplaySortRank(it.second, it.first).third }
            )
            .map { it.second }

        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            displayMissions.forEach { m ->
                val diff = m.currentPrice - m.entryPrice
                val diffPct = (diff / m.entryPrice) * 100.0 * (if (m.type == "LONG") 1.0 else -1.0)
                val isLoss = diffPct < 0
                val sign = if (isLoss) "" else "+"
                
                val elapsedMs = System.currentTimeMillis() - m.startTime
                val h = elapsedMs / (1000 * 60 * 60)
                val min = (elapsedMs / (1000 * 60)) % 60
                
                MissionTerminalCard(
                    missionId = m.id,
                    coinSymbol = m.coinSymbol,
                    type = m.type,
                    marketType = m.marketType,
                    originalEntry = "$${String.format("%.4f", m.originalSignalEntry)}",
                    entryPrice = "$${String.format("%.4f", m.entryPrice)}",
                    currentPrice = "$${String.format("%.4f", m.currentPrice)}",
                    roi = "$sign${String.format("%.2f", diffPct)}%",
                    timeElapsed = String.format("%02dh %02dm", h, min),
                    targets = m.targets,
                    stopLoss = m.stopLoss,
                    setupMode = m.setupMode,
                    tp1 = m.tp1,
                    tp2 = m.tp2,
                    tp3 = m.tp3,
                    manualStopLoss = m.manualStopLoss,
                    leverage = m.leverage,
                    riskProfile = m.riskProfile,
                    aiStatus = if (isBengali) m.aiStatusBengali else m.aiStatusEnglish,
                    confidence = m.confidence,
                    isNegative = isLoss,
                    isBengali = isBengali,
                    isHistory = false,
                    onStop = { overrideValue -> viewModel.stopMission(m.id, overrideValue) },
                    mission = m,
                    viewModel = viewModel
                )
            }
        }
    }
}
@Composable
fun HistoryMissionsContent(viewModel: CryptoViewModel, isBengali: Boolean) {
    val history by viewModel.missionHistory.collectAsState()
    
    if (history.isEmpty()) {
        EmptyMissionTerminal(isBengali)
    } else {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            history.forEach { m ->
                val diff = m.currentPrice - m.entryPrice
                val diffPct = (diff / m.entryPrice) * 100.0 * (if (m.type == "LONG") 1.0 else -1.0)
                val isLoss = diffPct < 0
                val sign = if (isLoss) "" else "+"
                
                val elapsedMs = System.currentTimeMillis() - m.startTime
                val h = elapsedMs / (1000 * 60 * 60)
                val min = (elapsedMs / (1000 * 60)) % 60
                
                MissionTerminalCard(
                    missionId = m.id,
                    coinSymbol = m.coinSymbol,
                    type = m.type,
                    marketType = m.marketType,
                    originalEntry = "$${String.format("%.4f", m.originalSignalEntry)}",
                    entryPrice = "$${String.format("%.4f", m.entryPrice)}",
                    currentPrice = "$${String.format("%.4f", m.currentPrice)}",
                    roi = "$sign${String.format("%.2f", diffPct)}%",
                    timeElapsed = String.format("%02dh %02dm", h, min),
                    targets = m.targets,
                    stopLoss = m.stopLoss,
                    setupMode = m.setupMode,
                    tp1 = m.tp1,
                    tp2 = m.tp2,
                    tp3 = m.tp3,
                    manualStopLoss = m.manualStopLoss,
                    leverage = m.leverage,
                    riskProfile = m.riskProfile,
                    aiStatus = (if (isBengali) "মিশন সম্পন্ন হয়েছে" else "MISSION COMPLETED") + " - ${if (isLoss) "LOSS" else "PROFIT"}",
                    confidence = m.confidence,
                    isNegative = isLoss,
                    isBengali = isBengali,
                    isHistory = true,
                    onStop = {}, // No-op in history
                    mission = m,
                    viewModel = viewModel
                )
            }
        }
    }
}
@Composable
fun EmptyMissionTerminal(isBengali: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .border(0.5.dp, T_BorderHigh, RectangleShape)
            .background(T_Surface)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isBengali) "কোনো সক্রিয় মিশন নেই" else "NO ACTIVE MISSIONS",
                color = T_TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isBengali) "প্রতীক্ষিত কমান্ড" else "AWAITING COMMAND...",
                color = T_TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
@Composable
fun CompactDataRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = T_TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = valueColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}
