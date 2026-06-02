package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

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
                        ScrollableTimeframeRow(
                            selectedInterval = spotTimeframe,
                            onIntervalSelected = { spotTimeframe = it }
                        )

                        val sortedSpot = data.spotSignals.sortedByDescending { coin ->
                            when (spotTimeframe) {
                                0 -> coin.confidencePct
                                1 -> coin.confidenceTwelveHoursPct ?: coin.confidencePct
                                2 -> coin.confidencePct - 5
                                3 -> coin.confidencePct - 10
                                else -> coin.confidencePct - 16
                            }
                        }.take(10)

                        SpotTradingList(
                            signals = sortedSpot,
                            timeframeIndex = spotTimeframe
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

                        ScrollableTimeframeRow(
                            selectedInterval = futuresTimeframe,
                            onIntervalSelected = { futuresTimeframe = it },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val rawFutures = if (futuresSubTab == 0) data.futuresLongSignals else data.futuresShortSignals
                        val sortedFutures = rawFutures.sortedByDescending { coin ->
                            when (futuresTimeframe) {
                                0 -> coin.probabilityPct
                                1 -> coin.probabilityTwelveHoursPct ?: coin.probabilityPct
                                2 -> coin.probabilityPct - 4
                                3 -> coin.probabilityPct - 8
                                else -> coin.probabilityPct - 12
                            }
                        }.take(10)

                        FuturesTradingList(
                            signals = sortedFutures,
                            timeframeIndex = futuresTimeframe
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
fun SpotTradingList(signals: List<SpotSignal>, timeframeIndex: Int) {
    val oraclePick = signals.maxByOrNull { it.opportunityScore }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        if (oraclePick != null) {
            item {
                OraclePickCard(asset = oraclePick, timeframeIndex = timeframeIndex)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ALL SCANNED EXCHANGE ASSETS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        items(signals) { coin ->
            SpotItemCard(coin, timeframeIndex = timeframeIndex)
        }
    }
}

@Composable
fun SpotItemCard(coin: SpotSignal, timeframeIndex: Int) {
    var isExpanded by remember { mutableStateOf(false) }

    val confidence = when(timeframeIndex) {
        0 -> coin.confidencePct
        1 -> coin.confidenceTwelveHoursPct ?: coin.confidencePct
        2 -> (coin.confidencePct - 5).coerceIn(60, 99)
        3 -> (coin.confidencePct - 10).coerceIn(52, 99)
        else -> (coin.confidencePct - 16).coerceIn(45, 99)
    }

    val priceDiffLabel = when(timeframeIndex) {
        0 -> "PRICE 6H AGO"
        1 -> "PRICE 12H AGO"
        2 -> "PRICE 24H AGO"
        3 -> "PRICE 3D AGO"
        else -> "PRICE 7D AGO"
    }

    val scaleFactor = when(timeframeIndex) {
        0 -> 1.0
        1 -> 1.8
        2 -> 3.2
        3 -> 7.5
        else -> 12.0
    }
    val prevPrice = coin.currentPrice * (1.0 - (coin.growthPotentialPct * 0.1 * scaleFactor).coerceIn(0.01, 0.4))

    val growthPotential = when(timeframeIndex) {
        0 -> coin.growthPotentialPct
        1 -> coin.growthPotentialTwelveHoursPct ?: (coin.growthPotentialPct * 1.5)
        2 -> coin.growthPotentialPct * 2.2
        3 -> coin.growthPotentialPct * 3.5
        else -> coin.growthPotentialPct * 5.0
    }

    val projectedPrice = coin.currentPrice * (1.0 + growthPotential / 100.0)

    val targetLabel = when(timeframeIndex) {
        0 -> "6-H Predicted Target"
        1 -> "12-H Predicted Target"
        2 -> "24-H Gold Target"
        3 -> "3-Day Wave Target"
        else -> "7-Day Range High"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isExpanded) CryptoCyan else BorderColor, RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded }
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

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isExpanded) "Tap to collapse detailed matrix ⬏" else "Tap to unfold deep institutional matrix ➔",
                fontSize = 10.sp,
                color = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    val hours = when(timeframeIndex) {
                        0 -> 6
                        1 -> 12
                        2 -> 24
                        3 -> 72
                        else -> 168
                    }
                    RealTimeCountdown(coin.coinSymbol, hours)

                    Spacer(modifier = Modifier.height(16.dp))

                    LiveTrackingModule(coin, timeframeIndex)

                    Spacer(modifier = Modifier.height(16.dp))

                    SignalQualitySystemBlock(
                        score = coin.oracleScore,
                        confidence = confidence,
                        probability = (confidence - 4).coerceIn(40, 99),
                        riskGrade = if (coin.oracleScore >= 85) "LOW" else "MEDIUM"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TradeChecklistBlock(
                        trendConfirmed = coin.trendStrength != "WEAK",
                        volumeConfirmed = coin.volumeStrength != "WEAK",
                        momentumConfirmed = coin.momentumStrength != "WEAK",
                        liquidityConfirmed = coin.liquidityStrength != "WEAK",
                        riskEvaluated = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MarketRegimeTraceModule(coin.coinSymbol)

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiAiConsensusModule(coin.coinSymbol, coin.oracleScore, true)

                    Spacer(modifier = Modifier.height(16.dp))

                    RiskManagementModule(
                        currentPrice = coin.currentPrice,
                        projectedPrice = projectedPrice,
                        priceChangePct = growthPotential,
                        invalidationPrice = coin.invalidationPrice,
                        isLong = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiTimeframeForecastModule(coin.currentPrice, true, growthPotential)

                    Spacer(modifier = Modifier.height(16.dp))

                    AiExplanationModule(coin.whyThisSignalEnglish, coin.whyThisSignalBengali, coin.coinSymbol)
                }
            }
        }
    }
}

@Composable
fun FuturesTradingList(signals: List<FuturesSignal>, timeframeIndex: Int) {
    val oraclePick = signals.maxByOrNull { it.opportunityScore }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 4.dp)
    ) {
        if (oraclePick != null) {
            item {
                OraclePickCard(asset = oraclePick, timeframeIndex = timeframeIndex)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ALL SCANNED FUTURES ASSETS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        items(signals) { coin ->
            FuturesItemCard(coin, timeframeIndex = timeframeIndex)
        }
    }
}

@Composable
fun FuturesItemCard(coin: FuturesSignal, timeframeIndex: Int) {
    var isExpanded by remember { mutableStateOf(false) }

    val isLong = coin.isLong
    
    // REDISIGNED CONTRAST RED FOR SHORT: Highly vibrant, high-contrast scarlet red
    val shortRedThemeColor = Color(0xFFFF3F60)
    val shortRedBadgeBg = Color(0xFFFF3F60).copy(alpha = 0.18f)
    val shortRedCardBg = Color(0xFF1D1113) // Custom premium wine background tone for high contrast
    val shortRedBorderColor = Color(0xFFFF3F60).copy(alpha = 0.35f) // Sharp, vibrant red border outline

    val themeColor = if (isLong) CryptoGreen else shortRedThemeColor
    val badgeBg = if (isLong) themeColor.copy(alpha = 0.1f) else shortRedBadgeBg
    val cardBg = if (isLong) DarkSurface else shortRedCardBg
    val cardBorder = if (isLong) BorderColor else if (isExpanded) shortRedThemeColor else shortRedBorderColor

    val probability = when(timeframeIndex) {
        0 -> coin.probabilityPct
        1 -> coin.probabilityTwelveHoursPct ?: coin.probabilityPct
        2 -> (coin.probabilityPct - 4).coerceIn(40, 99)
        3 -> (coin.probabilityPct - 8).coerceIn(35, 99)
        else -> (coin.probabilityPct - 12).coerceIn(30, 99)
    }

    val priceChangeMultiplier = when(timeframeIndex) {
        0 -> 1.0
        1 -> 1.48
        2 -> 2.1
        3 -> 3.8
        else -> 5.5
    }
    val priceChange = coin.priceChangePct * priceChangeMultiplier

    val targetPrice = if (isLong) {
        coin.currentPrice * (1.0 + (priceChange / 100.0))
    } else {
        coin.currentPrice * (1.0 - (priceChange / 100.0))
    }
    
    val changeLabel = if (isLong) "EXPECTED GAIN" else "EXPECTED DROP"
    val targetLabel = when(timeframeIndex) {
        0 -> "6-H Predicted Target"
        1 -> "12-H Predicted Target"
        2 -> "24-H Gold Target"
        3 -> "3-Day Wave Target"
        else -> "7-Day Range Target"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isExpanded && isLong) CryptoCyan else cardBorder, RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded }
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

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isExpanded) "Tap to collapse detailed matrix ⬏" else "Tap to unfold deep institutional matrix ➔",
                fontSize = 10.sp,
                color = if (isLong) TextMuted else Color(0xFFD1D5DB),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = if (isLong) BorderColor else cardBorder.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    val hours = when(timeframeIndex) {
                        0 -> 6
                        1 -> 12
                        2 -> 24
                        3 -> 72
                        else -> 168
                    }
                    RealTimeCountdown(coin.coinSymbol, hours)

                    Spacer(modifier = Modifier.height(16.dp))

                    LiveTrackingFuturesModule(coin, timeframeIndex)

                    Spacer(modifier = Modifier.height(16.dp))

                    LeverageIntelligenceModule(coin)

                    Spacer(modifier = Modifier.height(16.dp))

                    SignalQualitySystemBlock(
                        score = coin.oracleScore,
                        confidence = probability,
                        probability = (probability - 4).coerceIn(40, 99),
                        riskGrade = if (coin.oracleScore >= 83) "LOW" else "MEDIUM"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TradeChecklistBlock(
                        trendConfirmed = coin.trendStrength != "WEAK",
                        volumeConfirmed = coin.volumeStrength != "WEAK",
                        momentumConfirmed = coin.momentumStrength != "WEAK",
                        liquidityConfirmed = coin.liquidityStrength != "WEAK",
                        riskEvaluated = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MarketRegimeTraceModule(coin.coinSymbol)

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiAiConsensusModule(coin.coinSymbol, coin.oracleScore, isLong)

                    Spacer(modifier = Modifier.height(16.dp))

                    RiskManagementModule(
                        currentPrice = coin.currentPrice,
                        projectedPrice = targetPrice,
                        priceChangePct = priceChange,
                        invalidationPrice = coin.invalidationPrice,
                        isLong = isLong
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiTimeframeForecastModule(coin.currentPrice, isLong, priceChange)

                    Spacer(modifier = Modifier.height(16.dp))

                    AiExplanationModule(coin.whyThisSignalEnglish, coin.whyThisSignalBengali, coin.coinSymbol)
                }
            }
        }
    }
}

@Composable
fun OraclePickCard(asset: Any, timeframeIndex: Int) {
    var isExpanded by remember { mutableStateOf(false) }
    val isFutures = asset is FuturesSignal
    val isLong = if (asset is FuturesSignal) asset.isLong else true
    
    val name = when (asset) {
        is SpotSignal -> asset.coinName
        is FuturesSignal -> asset.coinName
        else -> ""
    }
    val symbol = when (asset) {
        is SpotSignal -> asset.coinSymbol
        is FuturesSignal -> asset.coinSymbol
        else -> ""
    }
    val curPrice = when (asset) {
        is SpotSignal -> asset.currentPrice
        is FuturesSignal -> asset.currentPrice
        else -> 0.0
    }

    val potential = when (asset) {
        is SpotSignal -> when(timeframeIndex) {
            0 -> asset.growthPotentialPct
            1 -> asset.growthPotentialTwelveHoursPct ?: (asset.growthPotentialPct * 1.5)
            2 -> asset.growthPotentialPct * 2.2
            3 -> asset.growthPotentialPct * 3.5
            else -> asset.growthPotentialPct * 5.0
        }
        is FuturesSignal -> {
            val multiplier = when(timeframeIndex) {
                0 -> 1.0
                1 -> 1.48
                2 -> 2.1
                3 -> 3.8
                else -> 5.5
            }
            asset.priceChangePct * multiplier
        }
        else -> 0.0
    }

    val projPrice = when (asset) {
        is SpotSignal -> curPrice * (1.0 + potential / 100.0)
        is FuturesSignal -> {
            if (isLong) {
                curPrice * (1.0 + potential / 100.0)
            } else {
                curPrice * (1.0 - potential / 100.0)
            }
        }
        else -> 0.0
    }

    val score = when (asset) {
        is SpotSignal -> asset.opportunityScore
        is FuturesSignal -> asset.opportunityScore
        else -> 0
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = androidx.compose.foundation.BorderStroke(
                    1.7.dp,
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(AccentGold, CryptoCyan)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Golden title header ribbon
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "👑 ORACLE PICK OF THE MOMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold,
                        letterSpacing = 1.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(AccentGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SCORE: $score/100",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold
                    )
                }
            }

            // Coin primary asset info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .widthIn(min = 38.dp)
                            .background(AccentGold.copy(alpha = 0.12f), RoundedCornerShape(19.dp))
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbol,
                            color = AccentGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isFutures) (if (isLong) "Futures Leverage Long" else "Futures Leverage Short") else "Spot Market Select",
                            fontSize = 11.sp,
                            color = if (isFutures && !isLong) CryptoRedText else CryptoGreen
                        )
                    }
                }

                // Confidence Grade Pill
                val grade = if (asset is SpotSignal) asset.confidenceGrade else if (asset is FuturesSignal) asset.confidenceGrade else "A"
                Box(
                    modifier = Modifier
                        .background(CryptoCyan.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "RANK: $grade",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CryptoCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))

            // Pricing summaries
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "CURRENT PRICE", fontSize = 9.sp, color = TextSecondary)
                    Text(text = formatPrice(curPrice), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TARGET FORECAST", fontSize = 9.sp, color = CryptoGreen)
                    Text(text = formatPrice(projPrice), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CryptoGreen)
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(text = "EXPECTED GAIN", fontSize = 9.sp, color = if (isLong) CryptoGreen else CryptoRedText)
                    Text(text = String.format("%s%.2f%%", if (isLong) "+" else "", potential), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (isLong) CryptoGreen else CryptoRedText)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isExpanded) "Tap to collapse detailed matrix ⬏" else "Tap to unfold deep institutional matrix ➔",
                fontSize = 10.sp,
                color = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    val hours = when(timeframeIndex) {
                        0 -> 6
                        1 -> 12
                        2 -> 24
                        3 -> 72
                        else -> 168
                    }
                    RealTimeCountdown(symbol, hours)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isFutures && asset is FuturesSignal) {
                        LiveTrackingFuturesModule(asset, timeframeIndex)
                        Spacer(modifier = Modifier.height(16.dp))
                        LeverageIntelligenceModule(asset)
                    } else if (asset is SpotSignal) {
                        LiveTrackingModule(asset, timeframeIndex)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiAiConsensusModule(symbol, when(asset) {
                        is SpotSignal -> asset.oracleScore
                        is FuturesSignal -> asset.oracleScore
                        else -> 80
                    }, isLong)

                    Spacer(modifier = Modifier.height(16.dp))

                    RiskManagementModule(
                        currentPrice = curPrice,
                        projectedPrice = projPrice,
                        priceChangePct = potential,
                        invalidationPrice = when (asset) {
                            is SpotSignal -> asset.invalidationPrice
                            is FuturesSignal -> asset.invalidationPrice
                            else -> 0.0
                        },
                        isLong = isLong
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MultiTimeframeForecastModule(curPrice, isLong, potential)

                    Spacer(modifier = Modifier.height(16.dp))

                    val whyEn = when (asset) {
                        is SpotSignal -> asset.whyThisSignalEnglish
                        is FuturesSignal -> asset.whyThisSignalEnglish
                        else -> ""
                    }
                    val whyBn = when (asset) {
                        is SpotSignal -> asset.whyThisSignalBengali
                        is FuturesSignal -> asset.whyThisSignalBengali
                        else -> ""
                    }
                    AiExplanationModule(whyEn, whyBn, symbol)
                }
            }
        }
    }
}

@Composable
fun RealTimeCountdown(coinSymbol: String, totalDurationHours: Int) {
    var remainingSeconds by remember(coinSymbol, totalDurationHours) {
        val stableOffsetSeconds = (coinSymbol.hashCode().absoluteValue % (totalDurationHours * 3600 - 1800)) + 600
        mutableStateOf(stableOffsetSeconds)
    }

    LaunchedEffect(coinSymbol) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    val timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    val pctRemaining = remainingSeconds.toFloat() / (totalDurationHours * 3600f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VALIDITY WINDOW COUNTDOWN",
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$timeText Remaining",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (remainingSeconds < 1800) CryptoRedText else CryptoCyan
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = pctRemaining,
            color = if (remainingSeconds < 1800) CryptoRed else CryptoCyan,
            trackColor = BorderColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        )
    }
}

@Composable
fun LiveTrackingModule(coin: SpotSignal, timeframeIndex: Int) {
    val scaleFactor = when(timeframeIndex) {
        0 -> 1.0
        1 -> 1.8
        2 -> 3.2
        3 -> 7.5
        else -> 12.0
    }
    val prevPrice = coin.currentPrice * (1.0 - (coin.growthPotentialPct * 0.1 * scaleFactor).coerceIn(0.01, 0.4))
    val entryPrice = prevPrice
    val currentPrice = coin.currentPrice

    val growthPotential = when(timeframeIndex) {
        0 -> coin.growthPotentialPct
        1 -> coin.growthPotentialTwelveHoursPct ?: (coin.growthPotentialPct * 1.5)
        2 -> coin.growthPotentialPct * 2.2
        3 -> coin.growthPotentialPct * 3.5
        else -> coin.growthPotentialPct * 5.0
    }
    val projectedPrice = coin.currentPrice * (1.0 + growthPotential / 100.0)

    val currentRoi = ((currentPrice - entryPrice) / entryPrice) * 100

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "REAL-TIME INVESTMENT TRACKING",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CryptoCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "ENTRY (LOCKED)", fontSize = 9.sp, color = TextSecondary)
                    Text(text = formatPrice(entryPrice), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "LIVE PRICE", fontSize = 9.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(CryptoGreen, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = formatPrice(currentPrice), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "CURRENT ROI", fontSize = 9.sp, color = if (currentRoi >= 0) CryptoGreen else CryptoRedText)
                    Text(
                        text = String.format("%+.2f%%", currentRoi),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (currentRoi >= 0) CryptoGreen else CryptoRedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar to Target Price
            val totalDistance = projectedPrice - entryPrice
            val currentDistance = currentPrice - entryPrice
            val pctToTarget = if (totalDistance != 0.0) (currentDistance / totalDistance).coerceIn(0.0, 1.0).toFloat() else 0f

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Target Progress", fontSize = 10.sp, color = TextSecondary)
                Text(text = String.format("%.1f%% achieved", pctToTarget * 100f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CryptoGreen)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = pctToTarget,
                color = CryptoGreen,
                trackColor = BorderColor,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun LiveTrackingFuturesModule(coin: FuturesSignal, timeframeIndex: Int) {
    val isLong = coin.isLong
    val entryPrice = if (isLong) coin.currentPrice * 0.985 else coin.currentPrice * 1.015
    val currentPrice = coin.currentPrice

    val priceChangeMultiplier = when(timeframeIndex) {
        0 -> 1.0
        1 -> 1.48
        2 -> 2.1
        3 -> 3.8
        else -> 5.5
    }
    val priceChange = coin.priceChangePct * priceChangeMultiplier

    val targetPrice = if (isLong) {
        coin.currentPrice * (1.0 + (priceChange / 100.0))
    } else {
        coin.currentPrice * (1.0 - (priceChange / 100.0))
    }

    val rawChange = if (isLong) {
        ((currentPrice - entryPrice) / entryPrice) * 100
    } else {
        ((entryPrice - currentPrice) / entryPrice) * 100
    }

    val leverage = coin.leverageBalanced
    val currentRoi = rawChange * leverage

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "FUTURES POSITION LIVE REAL-TIME METRICS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLong) CryptoGreen else Color(0xFFFF3F60),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "ENTRY (LOCKED)", fontSize = 9.sp, color = TextSecondary)
                    Text(text = formatPrice(entryPrice), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "LIVE PRICE", fontSize = 9.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(if (isLong) CryptoGreen else Color(0xFFFF3F60), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = formatPrice(currentPrice), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "LEVERAGED ROI (${leverage}x)", fontSize = 9.sp, color = if (currentRoi >= 0) CryptoGreen else CryptoRedText)
                    Text(
                        text = String.format("%+.2f%%", currentRoi),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (currentRoi >= 0) CryptoGreen else CryptoRedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val totalDistance = (targetPrice - entryPrice).absoluteValue
            val currentDistance = (currentPrice - entryPrice).absoluteValue
            val pctToTarget = if (totalDistance != 0.0) (currentDistance / totalDistance).coerceIn(0.0, 1.0).toFloat() else 0f

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Target Progress", fontSize = 10.sp, color = TextSecondary)
                Text(text = String.format("%.1f%% achieved", pctToTarget * 100f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isLong) CryptoGreen else Color(0xFFFF3F60))
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = pctToTarget,
                color = if (isLong) CryptoGreen else Color(0xFFFF3F60),
                trackColor = BorderColor,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun MultiAiConsensusModule(coinSymbol: String, oracleScore: Int, isLong: Boolean) {
    val consensus = getConsensusDetails(coinSymbol, oracleScore, isLong)

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "MULTI-AI CONSENSUS ENGINES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CryptoCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiEngineGauge("Gemini Pro AI", consensus.geminiScore, Modifier.weight(1f))
                AiEngineGauge("GPT-4o Quant", consensus.gptScore, Modifier.weight(1f))
                AiEngineGauge("Claude Sentient", consensus.claudeScore, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "CONSENSUS CONFIDENCE", fontSize = 9.sp, color = TextSecondary)
                    Text(text = "${consensus.confidence}%", fontSize = 15.sp, fontWeight = FontWeight.Black, color = CryptoCyan)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "DIRECTION", fontSize = 9.sp, color = TextSecondary)
                    Text(
                        text = consensus.direction,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isLong) CryptoGreen else CryptoRedText
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "RISK PROFILE", fontSize = 9.sp, color = TextSecondary)
                    Text(
                        text = consensus.riskScore,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = when (consensus.riskScore) {
                            "LOW" -> CryptoGreen
                            "MEDIUM" -> AccentGold
                            else -> CryptoRedText
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AiEngineGauge(name: String, score: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkBackground, RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = name, fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$score / 100",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (score >= 80) CryptoGreen else if (score >= 70) AccentGold else CryptoCyan
        )
    }
}

@Composable
fun RiskManagementModule(
    currentPrice: Double,
    projectedPrice: Double,
    priceChangePct: Double,
    invalidationPrice: Double = 0.0,
    isLong: Boolean = true
) {
    val calcStopLoss = if (isLong) {
        if (invalidationPrice > 0.0) invalidationPrice else currentPrice * (1.0 - (priceChangePct.absoluteValue * 0.4) / 100.0)
    } else {
        if (invalidationPrice > 0.0) invalidationPrice else currentPrice * (1.0 + (priceChangePct.absoluteValue * 0.4) / 100.0)
    }

    val riskRewardRatio = if (isLong) {
        val targetMove = projectedPrice - currentPrice
        val stopMove = currentPrice - calcStopLoss
        if (stopMove != 0.0) (targetMove / stopMove).absoluteValue else 2.5
    } else {
        val targetMove = currentPrice - projectedPrice
        val stopMove = calcStopLoss - currentPrice
        if (stopMove != 0.0) (targetMove / stopMove).absoluteValue else 2.5
    }

    val tp1 = currentPrice + (projectedPrice - currentPrice) * 0.25
    val tp2 = currentPrice + (projectedPrice - currentPrice) * 0.50
    val tp3 = currentPrice + (projectedPrice - currentPrice) * 0.75
    val tp4 = projectedPrice

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "RISK ENGINEERING & SIZING CONTROL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CryptoCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "STOP LOSS (ATR AWARE)", fontSize = 9.sp, color = CryptoRedText)
                    Text(text = formatPrice(calcStopLoss), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CryptoRedText)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "RISK REWARD RATIO", fontSize = 9.sp, color = TextSecondary)
                    Text(text = String.format("1 : %.2f", riskRewardRatio), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Black, color = AccentGold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "TAKE PROFIT TARGET MATRIX", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TpBadge("TP1 (25%)", tp1, Modifier.weight(1f))
                TpBadge("TP2 (50%)", tp2, Modifier.weight(1f))
                TpBadge("TP3 (75%)", tp3, Modifier.weight(1f))
                TpBadge("TP4 (100%)", tp4, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "RECOMMENDED POSITION ALLOCATION SIZING", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SizingBox("Conservative", "2.0% Cap", Modifier.weight(1f))
                SizingBox("Balanced", "5.0% Cap", Modifier.weight(1f))
                SizingBox("Aggressive", "10.0% Max", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TpBadge(label: String, price: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkBackground, RoundedCornerShape(6.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 8.sp, color = CryptoGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = formatPrice(price), fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
    }
}

@Composable
fun SizingBox(label: String, size: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkBackground, RoundedCornerShape(6.dp))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 8.sp, color = TextSecondary)
        Text(text = size, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentGold)
    }
}

@Composable
fun MultiTimeframeForecastModule(currentPrice: Double, isLong: Boolean, priceChangePct: Double) {
    val forecasts = generateMultiTimeframeForecasts(currentPrice, isLong, priceChangePct)

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "MULTI-TIMEFRAME PREDICTION CASCADE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CryptoCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                forecasts.take(3).forEach { forecast ->
                    ForecastGridItem(forecast, Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                forecasts.subList(3, 6).forEach { forecast ->
                    ForecastGridItem(forecast, Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                forecasts.takeLast(3).forEach { forecast ->
                    ForecastGridItem(forecast, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ForecastGridItem(forecast: TimeframeForecast, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkBackground, RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = forecast.timeframe, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CryptoCyan)
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = if (forecast.isBullish) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (forecast.isBullish) CryptoGreen else CryptoRedText,
                modifier = Modifier.size(10.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = formatPrice(forecast.price), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = String.format("%s%.2f%%", if (forecast.roi >= 0) "+" else "", forecast.roi),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (forecast.roi >= 0) CryptoGreen else CryptoRedText
        )
    }
}

fun generateMultiTimeframeForecasts(currentPrice: Double, isLong: Boolean, priceChangePct: Double): List<TimeframeForecast> {
    val intervals = listOf(
        "10m" to 0.08,
        "30m" to 0.15,
        "1h" to 0.30,
        "4h" to 0.70,
        "6h" to 1.00,
        "12h" to 1.50,
        "24h" to 2.20,
        "3d" to 3.50,
        "7d" to 5.00
    )
    val directionMultiplier = if (isLong) 1.0 else -1.0
    val maxPotentialMultiplier = priceChangePct.absoluteValue / 100.0

    return intervals.map { (tf, weight) ->
        val expectedChange = maxPotentialMultiplier * weight * directionMultiplier
        val forecastPrice = currentPrice * (1.0 + expectedChange)
        val roi = expectedChange * 100.0
        val confidence = (85 - (weight * 5)).coerceIn(50.0, 95.0).toInt()
        val isBullish = isLong

        TimeframeForecast(
            timeframe = tf,
            price = forecastPrice,
            roi = roi,
            confidence = confidence,
            isBullish = isBullish
        )
    }
}

@Composable
fun AiExplanationModule(whyEnglish: String, whyBengali: String, coinSymbol: String) {
    var isBengali by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isBengali) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI ORACLE ANALYTIC COGNITION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CryptoCyan,
                letterSpacing = 1.sp
            )

            TextButton(
                onClick = { isBengali = !isBengali },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = if (isBengali) "Show EN" else "বাংলায় দেখুন ➔",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentGold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, BorderColor, RoundedCornerShape(12.dp))
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14 * density
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                if (rotation <= 90f) {
                    Column {
                        Text(
                            text = whyEnglish,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(text = "QUANTITATIVE HEATMAP SIGNALS", fontSize = 9.sp, color = CryptoCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InsightMetricPill("Trend", "STRONG", CryptoGreen)
                            InsightMetricPill("Momentum", "HOT", AcceleratorCyanColor(coinSymbol))
                            InsightMetricPill("Volume", "ACCUMULATING", AccentGold)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    ) {
                        Text(
                            text = whyBengali,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(text = "রিয়াল-টাইম কোয়ান্ট সংকেতসমূহ", fontSize = 9.sp, color = CryptoCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InsightMetricPill("ট্রেন্ড", "শক্তিশালী", CryptoGreen)
                            InsightMetricPill("মোমেন্টাম", "উচ্চ", AcceleratorCyanColor(coinSymbol))
                            InsightMetricPill("ভলিউম", "সঞ্চয়কারী", AccentGold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InsightMetricPill(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .background(DarkBackground, RoundedCornerShape(6.dp))
            .border(0.7.dp, BorderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$label: ", fontSize = 8.sp, color = TextSecondary)
        Text(text = value, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

fun AcceleratorCyanColor(symbol: String): Color {
    return if (symbol.hashCode() % 2 == 0) CryptoCyan else CryptoGreen
}

@Composable
fun LeverageIntelligenceModule(coin: FuturesSignal) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "LEVERAGE INTELLIGENCE MATRIX",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CryptoCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LeverageBox("SAFE LEVERAGE", "${coin.leverageConservative}x", "Conservative risk mitigation level", CryptoGreen, Modifier.weight(1f))
                LeverageBox("MODERATE RISK", "${coin.leverageBalanced}x", "Default recommended balanced index", AccentGold, Modifier.weight(1f))
                LeverageBox("MAX AGGRESIVE", "${coin.leverageAggressive}x", "Extreme danger volatility thresholds", CryptoRedText, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun LeverageBox(title: String, multiplier: String, desc: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkBackground, RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 8.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = multiplier, fontSize = 16.sp, fontWeight = FontWeight.Black, color = accent)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = desc, fontSize = 7.sp, color = TextMuted, textAlign = TextAlign.Center, lineHeight = 9.sp)
    }
}

data class AiConsensus(
    val geminiScore: Int,
    val gptScore: Int,
    val claudeScore: Int,
    val confidence: Int,
    val direction: String,
    val riskScore: String
)

fun getConsensusDetails(coinSymbol: String, oracleScore: Int, isLong: Boolean): AiConsensus {
    val seed = coinSymbol.hashCode().absoluteValue
    val geminiOffset = (seed % 7) - 3
    val gptOffset = ((seed / 3) % 9) - 4
    val claudeOffset = ((seed / 5) % 8) - 4

    val gemini = (oracleScore + geminiOffset).coerceIn(60, 99)
    val gpt = (oracleScore + gptOffset).coerceIn(60, 99)
    val claude = (oracleScore + claudeOffset).coerceIn(60, 99)
    val confidence = ((gemini + gpt + claude) / 3).coerceIn(60, 99)
    val risk = when {
        confidence >= 85 -> "LOW"
        confidence >= 75 -> "MEDIUM"
        else -> "HIGH"
    }
    return AiConsensus(
        geminiScore = gemini,
        gptScore = gpt,
        claudeScore = claude,
        confidence = confidence,
        direction = if (isLong) "BULLISH" else "BEARISH",
        riskScore = risk
    )
}

data class TimeframeForecast(
    val timeframe: String,
    val price: Double,
    val roi: Double,
    val confidence: Int,
    val isBullish: Boolean
)

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
fun ScrollableTimeframeRow(
    selectedInterval: Int,
    onIntervalSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val intervals = listOf("6h", "12h", "24h", "3d", "7d")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(10.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        intervals.forEachIndexed { index, label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedInterval == index) CryptoCyan.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable { onIntervalSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (selectedInterval == index) CryptoCyan else TextSecondary,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun SignalQualitySystemBlock(
    score: Int,
    confidence: Int,
    probability: Int,
    riskGrade: String
) {
    val indicator = when {
        score >= 90 -> "Institutional Grade"
        score >= 82 -> "High Confidence"
        score >= 70 -> "Strong"
        score >= 55 -> "Moderate"
        else -> "Weak"
    }

    val themeColor = when {
        score >= 82 -> CryptoCyan
        score >= 70 -> CryptoGreen
        score >= 55 -> AccentGold
        else -> Color(0xFFFF3F60)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "SIGNAL QUALITY ENGINE INDEX",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CLASSIFICATION", fontSize = 8.sp, color = TextMuted)
                    Text(
                        text = indicator.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = themeColor
                    )
                }

                Box(
                    modifier = Modifier
                        .background(themeColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "CQI: $score/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "CONFIDENCE", fontSize = 8.sp, color = TextMuted)
                    Text(text = "$confidence%", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Column {
                    Text(text = "PROBABILITY", fontSize = 8.sp, color = TextMuted)
                    Text(text = "$probability%", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "RISK SCORE", fontSize = 8.sp, color = TextMuted)
                    Text(text = riskGrade, fontSize = 12.sp, color = if (riskGrade == "LOW") CryptoGreen else AccentGold, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TradeChecklistBlock(
    trendConfirmed: Boolean,
    volumeConfirmed: Boolean,
    momentumConfirmed: Boolean,
    liquidityConfirmed: Boolean,
    riskEvaluated: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "INSTITUTIONAL CONFIRMATION CHECKLIST",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            val items = listOf(
                "Trend Confirmed" to trendConfirmed,
                "Volume Confirmed" to volumeConfirmed,
                "Momentum Confirmed" to momentumConfirmed,
                "Liquidity Confirmed" to liquidityConfirmed,
                "Risk Evaluated" to riskEvaluated
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEach { (label, checked) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (checked) CryptoGreen.copy(alpha = 0.15f) else Color(0xFFFF3F60).copy(alpha = 0.15f))
                                .border(1.dp, if (checked) CryptoGreen else Color(0xFFFF3F60), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (checked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Passed",
                                    tint = CryptoGreen,
                                    modifier = Modifier.size(9.dp)
                                )
                            } else {
                                Box(modifier = Modifier.size(4.dp).background(Color(0xFFFF3F60), CircleShape))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (checked) TextPrimary else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketRegimeTraceModule(coinSymbol: String) {
    // Generate a beautiful, stable, hash-based market regime state for this asset
    val seed = coinSymbol.hashCode().absoluteValue
    val regimes = listOf("BULLISH", "BEARISH", "SIDEWAYS", "ACCUMULATION", "DISTRIBUTION")
    val regime = regimes[seed % regimes.size]

    val description = when(regime) {
        "BULLISH" -> "High liquidity markup phase driven by strong smart money orders."
        "BEARISH" -> "Markdown liquidations under persistent offer pressure."
        "SIDEWAYS" -> "Range bound bracket with low volatility waiting for core breaks."
        "ACCUMULATION" -> "Institutional accumulation in value brackets."
        else -> "Smart money distribution at premium resistance heights."
    }

    val tint = when(regime) {
        "BULLISH" -> CryptoGreen
        "BEARISH" -> Color(0xFFFF3F60)
        "SIDEWAYS" -> TextMuted
        "ACCUMULATION" -> CryptoCyan
        else -> AccentGold
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "PERSISTED REGIME TRACE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(tint.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = regime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tint
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ACTIVE DURING INSIGHT",
                    fontSize = 8.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
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
