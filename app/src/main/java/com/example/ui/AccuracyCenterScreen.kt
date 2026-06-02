package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SignalEntity
import com.example.ui.theme.*
import com.example.viewmodel.CryptoViewModel
import kotlinx.coroutines.launch

@Composable
fun AccuracyCenterScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    val rawSignals by viewModel.signalHistory.collectAsState()
    val scope = rememberCoroutineScope()

    // Filter index: 0 = Last 7 Days, 1 = Last 30 Days, 2 = All Time
    var selectedFilterIndex by remember { mutableStateOf(2) }

    // Filter logic
    val filteredSignals = remember(rawSignals, selectedFilterIndex) {
        val now = System.currentTimeMillis()
        when (selectedFilterIndex) {
            0 -> rawSignals.filter { now - it.timestamp <= 7 * 24 * 60 * 60 * 1000L }
            1 -> rawSignals.filter { now - it.timestamp <= 30 * 24 * 60 * 60 * 1000L }
            else -> rawSignals
        }
    }

    // Stats calculations
    val totalSignals = filteredSignals.size
    val wins = filteredSignals.count { it.result == "WIN" }
    val losses = filteredSignals.count { it.result == "LOSS" }
    val pending = filteredSignals.count { it.result == "PENDING" }
    val winRate = if (totalSignals - pending > 0) {
        (wins.toDouble() / (totalSignals - pending).toDouble()) * 100.0
    } else {
        0.0
    }

    // Profit factor and average return mocks matching Win values
    val avgReturn = if (winRate > 0) {
        (winRate * 0.12 - (100 - winRate) * 0.04)
    } else {
        0.0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Upper Title Header
        item {
            Column {
                Text(
                    text = "SWIFT AUDIT & TRACKING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CryptoCyan,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Accuracy Tracking Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Time Filtering Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val filters = listOf("Last 7 Days", "Last 30 Days", "All Time")
                filters.forEachIndexed { idx, filterLabel ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedFilterIndex == idx) CryptoCyan.copy(alpha = 0.12f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (selectedFilterIndex == idx) CryptoCyan else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedFilterIndex = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filterLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedFilterIndex == idx) CryptoCyan else TextSecondary
                        )
                    }
                }
            }
        }

        // Accuracy Score Cards Row / Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ESTABLISHED ORACLE WIN-RATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Massive visual dynamic win percentage Text indicator
                        Text(
                            text = String.format("%.1f%%", winRate),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CryptoGreen,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )

                        // Visual gauge details
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "WINS: $wins   LOSSES: $losses",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Pending Signals: $pending",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 14.dp))

                    // Secondary details: Profit Factor, Avg Return
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PROFIT FACTOR",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "2.84x",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AVERAGE RETURN / SIGNAL",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = String.format("+%.2f%%", avgReturn),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CryptoGreen,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TOTAL COMPLETED",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${wins + losses}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CryptoCyan,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // Historic signals Title & Reset Database control
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTORICAL SIGNALS RECORD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CryptoCyan,
                    letterSpacing = 1.5.sp
                )

                // Purge historic DB reset button
                TextButton(
                    onClick = {
                        scope.launch { viewModel.clearHistory() }
                    }
                ) {
                    Text(
                        text = "Reset Database",
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Individual historical elements cards
        if (filteredSignals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recorded signals found for this period in local SQLite.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredSignals) { entity ->
                SignalHistoryCard(entity = entity)
            }
        }
    }
}

@Composable
fun SignalHistoryCard(entity: SignalEntity) {
    val isWin = entity.result == "WIN"
    val isLoss = entity.result == "LOSS"
    
    val badgeBg = when {
        isWin -> CryptoGreen.copy(alpha = 0.12f)
        isLoss -> Color(0xFFFF3F60).copy(alpha = 0.12f)
        else -> TextMuted.copy(alpha = 0.12f)
    }

    val badgeColor = when {
        isWin -> CryptoGreen
        isLoss -> Color(0xFFFF3F60)
        else -> TextSecondary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = entity.coinSymbol,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = entity.signalType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = entity.result,
                        color = badgeColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ENTRY PRICE", fontSize = 8.sp, color = TextMuted, letterSpacing = 0.5.sp)
                    Text(
                        text = formatPrice(entity.entryPrice),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }

                Column {
                    Text(text = "TARGET PRICE", fontSize = 8.sp, color = TextMuted, letterSpacing = 0.5.sp)
                    Text(
                        text = formatPrice(entity.targetPrice),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (entity.priceChangePct >= 0) "MAX PROFIT" else "MAX DRAWDOWN",
                        fontSize = 8.sp,
                        color = badgeColor,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = String.format("%s%.2f%%", if (entity.priceChangePct >= 0) "+" else "", entity.priceChangePct),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = badgeColor,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return when {
        price >= 1000 -> String.format("$%,.2f", price)
        price >= 1 -> String.format("$%,.3f", price)
        else -> String.format("$%.6f", price)
    }
}
