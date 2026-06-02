package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FuturesSignal
import com.example.model.OracleAnalysisResponse
import com.example.model.SpotSignal
import com.example.ui.theme.*
import com.example.viewmodel.AnalysisState
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CryptoViewModel

@Composable
fun AnalysisScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    val analysisState by viewModel.analysisState.collectAsState()

    androidx.activity.compose.BackHandler {
        viewModel.navigateTo(AppScreen.Home)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when (val state = analysisState) {
            is AnalysisState.Idle -> {
                LaunchedEffect(Unit) {
                    viewModel.runScanner()
                }
            }
            is AnalysisState.Analyzing -> {
                AnalyzingTelemetryScreen(stepMessage = state.statusMessage)
            }
            is AnalysisState.Success -> {
                PredictionDashboard(
                    data = state.data,
                    viewModel = viewModel
                )
            }
            is AnalysisState.Error -> {
                ScannerErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.runScanner() },
                    onGoBack = { viewModel.navigateTo(AppScreen.Home) }
                )
            }
        }
    }
}

@Composable
fun AnalyzingTelemetryScreen(stepMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            CircularProgressIndicator(
                color = CryptoCyan,
                strokeWidth = 6.dp,
                modifier = Modifier.fillMaxSize()
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Calculating scanner metrics",
                tint = CryptoCyan,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "SCANNING SIGNAL MATRIX",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CryptoCyan,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stepMessage,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Futuristic decorative log box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(6.dp).background(CryptoGreen, CircleShape).align(Alignment.CenterVertically))
                Text(text = "RSI Relative indicators calculated", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(6.dp).background(CryptoGreen, CircleShape).align(Alignment.CenterVertically))
                Text(text = "MACD trend histograms synchronized", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(6.dp).background(CryptoCyan, CircleShape).align(Alignment.CenterVertically))
                Text(text = "Evaluating historical probability metrics", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            }
        }
    }
}

@Composable
fun PredictionDashboard(
    data: OracleAnalysisResponse,
    viewModel: CryptoViewModel
) {
    val selectedIndex by viewModel.selectedDashboardTab.collectAsState()
    var futuresSubTab by remember { mutableStateOf(0) }
    var spotTimeframe by remember { mutableStateOf(0) }
    var futuresTimeframe by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // App Custom Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.Home) },
                modifier = Modifier
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate to home",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "PREDICTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CryptoCyan,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Oracle Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            
            // Re-run scanner
            IconButton(
                onClick = { viewModel.runScanner() },
                modifier = Modifier
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Re-run scanner",
                    tint = CryptoCyan
                )
            }
        }

        // Category Tab Selectors (A: Spot Trading, B: Futures Trading)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            TabButton(
                title = "Spot Signals",
                isSelected = selectedIndex == 0,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setDashboardTab(0) }
            )
            TabButton(
                title = "Futures Signals",
                isSelected = selectedIndex == 1,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setDashboardTab(1) }
            )
        }

        // Dashboard Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            when (selectedIndex) {
                0 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(DarkSurface, RoundedCornerShape(10.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                .padding(2.dp)
                        ) {
                            TimeframeToggleButton(
                                title = "6-Hour Prediction",
                                isSelected = spotTimeframe == 0,
                                modifier = Modifier.weight(1f),
                                onClick = { spotTimeframe = 0 }
                            )
                            TimeframeToggleButton(
                                title = "12-Hour Prediction",
                                isSelected = spotTimeframe == 1,
                                modifier = Modifier.weight(1f),
                                onClick = { spotTimeframe = 1 }
                            )
                        }

                        SpotTradingList(
                            signals = if (spotTimeframe == 0) {
                                data.spotSignals.sortedByDescending { it.confidencePct }.take(10)
                            } else {
                                data.spotSignals.sortedByDescending { it.confidenceTwelveHoursPct ?: 0 }.take(10)
                            },
                            isTwelveHour = spotTimeframe == 1
                        )
                    }
                }
                1 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(2.dp)
                        ) {
                            SubTabButton(
                                title = "BUY LONG (Top 10)",
                                isSelected = futuresSubTab == 0,
                                isLongSelection = true,
                                modifier = Modifier.weight(1f),
                                onClick = { futuresSubTab = 0 }
                            )
                            SubTabButton(
                                title = "SELL SHORT (Top 10)",
                                isSelected = futuresSubTab == 1,
                                isLongSelection = false,
                                modifier = Modifier.weight(1f),
                                onClick = { futuresSubTab = 1 }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .background(DarkSurface, RoundedCornerShape(10.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                .padding(2.dp)
                        ) {
                            TimeframeToggleButton(
                                title = "6-Hour Prediction",
                                isSelected = futuresTimeframe == 0,
                                modifier = Modifier.weight(1f),
                                onClick = { futuresTimeframe = 0 }
                            )
                            TimeframeToggleButton(
                                title = "12-Hour Prediction",
                                isSelected = futuresTimeframe == 1,
                                modifier = Modifier.weight(1f),
                                onClick = { futuresTimeframe = 1 }
                            )
                        }

                        val rawFutures = if (futuresSubTab == 0) data.futuresLongSignals else data.futuresShortSignals
                        val sortedFutures = if (futuresTimeframe == 0) {
                            rawFutures.sortedByDescending { it.probabilityPct }.take(10)
                        } else {
                            rawFutures.sortedByDescending { it.probabilityTwelveHoursPct ?: 0 }.take(10)
                        }

                        FuturesTradingList(
                            signals = sortedFutures,
                            isTwelveHour = futuresTimeframe == 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CryptoCyan.copy(alpha = 0.15f) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) CryptoCyan else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) CryptoCyan else TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SubTabButton(
    title: String,
    isSelected: Boolean,
    isLongSelection: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val activeBgColor = if (isLongSelection) {
        if (isSelected) CryptoGreen.copy(alpha = 0.12f) else Color.Transparent
    } else {
        if (isSelected) Color(0xFFDC2626) else Color.Transparent
    }
    
    val textColor = if (isSelected) {
        if (isLongSelection) CryptoGreen else Color.White
    } else {
        TextSecondary
    }

    val borderModifier = if (isSelected && !isLongSelection) {
        Modifier.border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(6.dp))
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(activeBgColor)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SpotTradingList(signals: List<SpotSignal>, isTwelveHour: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        items(signals) { coin ->
            SpotItemCard(coin, isTwelveHour)
        }
    }
}

@Composable
fun SpotItemCard(coin: SpotSignal, isTwelveHour: Boolean) {
    val confidence = if (isTwelveHour) (coin.confidenceTwelveHoursPct ?: coin.confidencePct) else coin.confidencePct
    val priceDiffLabel = if (isTwelveHour) "PRICE 12H AGO" else "PRICE 6H AGO"
    val prevPrice = if (isTwelveHour) (coin.priceTwelveHoursAgo ?: coin.priceSixHoursAgo) else coin.priceSixHoursAgo
    val growthPotential = if (isTwelveHour) (coin.growthPotentialTwelveHoursPct ?: coin.growthPotentialPct) else coin.growthPotentialPct
    val projectedPrice = if (isTwelveHour) (coin.projectedPriceTwelveHours ?: coin.projectedPrice) else coin.projectedPrice
    val targetLabel = if (isTwelveHour) "12H Predicted Target" else "6-H Predicted Target"

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .widthIn(min = 36.dp)
                            .background(CryptoCyan.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = coin.coinSymbol,
                            color = CryptoCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (coin.coinSymbol.length > 3) 10.sp else 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = coin.coinName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Spot Market Asset",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(CryptoCyan.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confidence probability",
                        tint = CryptoCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$confidence% Probability",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CryptoCyan
                    )
                }
            }

            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))

            // Spot coin info (prior ago, Current, Growth %)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = priceDiffLabel,
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = formatPrice(prevPrice),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CURRENT PRICE",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = formatPrice(coin.currentPrice),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1.1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "GROWTH POTENTIAL",
                        fontSize = 9.sp,
                        color = CryptoGreen,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = String.format("+%.2f%%", growthPotential),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CryptoGreen,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target price prediction box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Projected upward potential trajectory",
                        tint = CryptoGreen,
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (-4).dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = targetLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatPrice(projectedPrice),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CryptoGreen,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun FuturesTradingList(signals: List<FuturesSignal>, isTwelveHour: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 4.dp)
    ) {
        items(signals) { coin ->
            FuturesItemCard(coin, isTwelveHour)
        }
    }
}

@Composable
fun FuturesItemCard(coin: FuturesSignal, isTwelveHour: Boolean) {
    val isLong = coin.isLong
    
    // REDISIGNED CONTRAST RED FOR SHORT: Highly vibrant, high-contrast scarlet red
    val shortRedThemeColor = Color(0xFFFF3F60)
    val shortRedBadgeBg = Color(0xFFFF3F60).copy(alpha = 0.18f)
    val shortRedCardBg = Color(0xFF1D1113) // Custom premium wine background tone for high contrast
    val shortRedBorderColor = Color(0xFFFF3F60).copy(alpha = 0.35f) // Sharp, vibrant red border outline

    val themeColor = if (isLong) CryptoGreen else shortRedThemeColor
    val badgeBg = if (isLong) themeColor.copy(alpha = 0.1f) else shortRedBadgeBg
    val cardBg = if (isLong) DarkSurface else shortRedCardBg
    val cardBorder = if (isLong) BorderColor else shortRedBorderColor

    val probability = if (isTwelveHour) (coin.probabilityTwelveHoursPct ?: coin.probabilityPct) else coin.probabilityPct
    val priceChange = if (isTwelveHour) (coin.priceChangeTwelveHoursPct ?: coin.priceChangePct) else coin.priceChangePct
    val targetPrice = if (isTwelveHour) (coin.targetPriceTwelveHours ?: coin.targetPrice) else coin.targetPrice
    
    val changeLabel = if (isLong) "EXPECTED GAIN" else "EXPECTED DROP"
    val targetLabel = if (isTwelveHour) "12H Predicted Target" else "6-H Predicted Target"

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .widthIn(min = 36.dp)
                            .background(badgeBg, RoundedCornerShape(18.dp))
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = coin.coinSymbol,
                            color = themeColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (coin.coinSymbol.length > 3) 10.sp else 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = coin.coinName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isLong) "Futures Long Position" else "Futures Short Position",
                            fontSize = 11.sp,
                            color = if (isLong) TextMuted else Color(0xFFD1D5DB) // Enhanced contrast
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isLong) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Signal trend direction",
                        tint = themeColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$probability% Confidence",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor
                    )
                }
            }

            HorizontalDivider(color = if (isLong) BorderColor else cardBorder.copy(alpha = 0.25f), modifier = Modifier.padding(vertical = 12.dp))

            // Price indicators rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: CURRENT PRICE (Left Aligned)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "CURRENT PRICE",
                        fontSize = 9.sp,
                        color = if (isLong) TextSecondary else Color(0xFFD1D5DB), // Enhanced high contrast grey
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = formatPrice(coin.currentPrice),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Column 2: EXPECTED GAIN/DROP (Center Aligned)
                Column(
                    modifier = Modifier.weight(1.1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = changeLabel,
                        fontSize = 9.sp,
                        color = themeColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = String.format("%s%.2f%%", if (isLong) "+" else "", priceChange),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Column 3: TARGET PRICE (Right Aligned)
                Column(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = targetLabel,
                        fontSize = 9.sp,
                        color = themeColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = formatPrice(targetPrice),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
fun TimeframeToggleButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CryptoCyan.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) CryptoCyan else TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

private fun formatPrice(price: Double): String {
    return when {
        price >= 1000 -> String.format("$%,.2f", price)
        price >= 1 -> String.format("$%,.3f", price)
        else -> String.format("$%.6f", price)
    }
}

@Composable
fun ScannerErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Scanner compilation error indicator",
            tint = CryptoRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SCANNER ANOMALY",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CryptoRed,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = CryptoCyan, contentColor = DarkBackground)
        ) {
            Text(text = "RE-CALIBRATE SCANNER", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onGoBack) {
            Text(text = "Return to Feed", color = TextSecondary)
        }
    }
}
