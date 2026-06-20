package com.example.feature.live_radar

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
import com.example.core.radar.RadarAlert
import com.example.feature.signal_pro.StartTradeFlow
import com.example.ui.theme.*
import com.example.viewmodel.CryptoViewModel
import kotlin.random.Random

@Composable
fun LiveRadarScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    val radarAlerts by viewModel.radarAlerts.collectAsState()
    val marketRegime by viewModel.marketRegime.collectAsState()
    val shortTermInterval by viewModel.shortTermTimeframe.collectAsState()

    val isBengali by viewModel.isBengali.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 12.dp),
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
                        color = LiveRadarSoftWhite
                    )
                }

                // Inline language switcher
                Button(
                    onClick = { viewModel.toggleLanguage() },
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
                        text = if (isBengali) "English" else "বাংলা",
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
                            .background(LiveRadarInstitutionalGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBengali) "অন-চেইন বাজার পরিস্থিতি" else "CURRENT MARKET REGIME",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = marketRegime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LiveRadarInstitutionalGreen,
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
                    text = if (isBengali) "সংক্ষিপ্ত সময়ের ওরাকল স্ক্যাল্পস" else "SHORT-TERM SCALP ORACLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiveRadarSoftWhite,
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
                ShortTermOpportunisticSignalsSection(timeframe = displayWindow, isBengali = isBengali, viewModel = viewModel)
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
                    text = if (isBengali) "তাত্ক্ষণিক পরিমাণগত রাডার বার্তা" else "REAL-TIME ALERTS FEED",
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
                        text = if (isBengali) "অটো-স্ক্যানিং" else "AUTO-SCANNING",
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
                        text = if (isBengali) "রাডার সক্রিয় করা হচ্ছে। নতুন সিগন্যালের সন্ধান চলছে..." else "Initializing quantum radar... searching for breakouts...",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(radarAlerts, key = { it.id }) { alert ->
                RadarAlertCard(alert = alert, isBengali = isBengali)
            }
        }
    }
}
