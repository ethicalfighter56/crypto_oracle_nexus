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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RadarAlert
import com.example.ui.theme.*
import com.example.viewmodel.CryptoViewModel
import kotlin.random.Random

@Composable
fun MarketRadarScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    val radarAlerts by viewModel.radarAlerts.collectAsState()
    val marketRegime by viewModel.marketRegime.collectAsState()
    val shortTermInterval by viewModel.shortTermTimeframe.collectAsState()

    var isBengaliMode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Toolbar Title Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE QUANT RADAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CryptoCyan,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Market Intelligence",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Inline language switcher
                Button(
                    onClick = { isBengaliMode = !isBengaliMode },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurface,
                        contentColor = CryptoCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isBengaliMode) "English" else "বাংলা",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Market Regime Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CryptoCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(CryptoGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBengaliMode) "অন-চেইন বাজার পরিস্থিতি" else "CURRENT MARKET REGIME",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = marketRegime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CryptoGreen,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Short-Term Period Selector Tabs
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = if (isBengaliMode) "সংক্ষিপ্ত সময়ের ওরাকল স্ক্যাল্পস" else "SHORT-TERM SCALP ORACLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBackground, RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val intervals = listOf("1 Min", "5 Min", "15 Min", "30 Min")
                    intervals.forEachIndexed { idx, label ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (shortTermInterval == idx) CryptoCyan.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (shortTermInterval == idx) CryptoCyan else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.setShortTermTimeframe(idx) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (shortTermInterval == idx) CryptoCyan else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Short Term Signals Display List
                val displayWindow = listOf("1M", "5M", "15M", "30M")[shortTermInterval]
                ShortTermOpportunisticSignalsSection(timeframe = displayWindow, isBengali = isBengaliMode)
            }
        }

        // Radar Alert Logs Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengaliMode) "তাত্ক্ষণিক পরিমাণগত রাডার বার্তা" else "REAL-TIME ALERTS FEED",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CryptoCyan,
                    letterSpacing = 1.5.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        color = CryptoCyan,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengaliMode) "অটো-স্ক্যানিং" else "AUTO-SCANNING",
                        fontSize = 9.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Radar Active Alerts Item List
        if (radarAlerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengaliMode) "রাডার সক্রিয় করা হচ্ছে। নতুন সিগন্যালের সন্ধান চলছে..." else "Initializing quantum radar... searching for breakouts...",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(radarAlerts, key = { it.id }) { alert ->
                RadarAlertCard(alert = alert, isBengali = isBengaliMode)
            }
        }
    }
}

@Composable
fun ShortTermOpportunisticSignalsSection(timeframe: String, isBengali: Boolean) {
    // Top 3 dynamic simulation metrics
    val spotScalps = listOf(
        Triple("Bitcoin", "BTC", 66520.0),
        Triple("Ethereum", "ETH", 3482.0),
        Triple("Solana", "SOL", 164.2)
    )

    val longScalps = listOf(
        Triple("Render Token", "RNDR", 8.45),
        Triple("NEAR Protocol", "NEAR", 6.12),
        Triple("Floki", "FLOKI", 0.000215)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isBengali) "🔥 তাত্ক্ষণিক স্পট টার্গেট (সেরা ৩)" else "🔥 HOT SPOT TRIGGERS (TOP 3)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            letterSpacing = 0.5.sp
        )

        spotScalps.forEachIndexed { index, (name, symbol, basePrice) ->
            val potential = 1.5 + index * 0.4
            val target = basePrice * (1.0 + potential / 100)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(AccentGold.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = symbol, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target: $${String.format("%,.2f", target)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CryptoGreen
                    )
                    Text(
                        text = "+${String.format("%.2f", potential)}% ($timeframe)",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isBengali) "⚡ ফিউচার লং টার্গেট" else "⚡ FUTURES LONG TRIGGERS (TOP 3)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = CryptoGreen,
            letterSpacing = 0.5.sp
        )

        longScalps.forEachIndexed { index, (name, symbol, basePrice) ->
            val potential = 3.2 + index * 0.8
            val target = basePrice * (1.0 + potential / 100)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(CryptoGreen.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = symbol, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CryptoGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target: $${String.format("%,.2f", target)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CryptoGreen
                    )
                    Text(
                        text = "Leverage: 5x | +${String.format("%.2f", potential)}%",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun RadarAlertCard(alert: RadarAlert, isBengali: Boolean) {
    val accentColor = when (alert.eventType) {
        "VOLUME_EXPLOSION" -> AccentGold
        "BREAKOUT" -> CryptoGreen
        "MOMENTUM_SURGE" -> CryptoCyan
        else -> Color(0xFFFF3F60) // Reddish
    }

    val badgeBg = accentColor.copy(alpha = 0.14f)

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
                            .clip(RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = alert.coinSymbol,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = alert.eventType.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = String.format("%.1f%% Magnitude", alert.magnitude),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isBengali) alert.descriptionBengali else alert.descriptionEnglish,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
