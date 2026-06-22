package com.example.feature.signal_pro

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.ui.graphics.Brush
import com.example.model.FuturesSignal
import com.example.model.OracleAnalysisResponse
import com.example.model.SpotSignal
import com.example.ui.theme.*
import com.example.viewmodel.AnalysisState
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CryptoViewModel
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.shadow

// Extracted from SignalProScreen.kt to keep the public screen entry point compact.
@Composable
fun AnalyzingTelemetryScreen(stepMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp)
        ) {
            CircularProgressIndicator(
                color = CryptoCyan,
                strokeWidth = 4.dp,
                modifier = Modifier.fillMaxSize()
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Calculating scanner metrics",
                tint = CryptoCyan,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SCANNING SIGNAL MATRIX",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CryptoCyan,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stepMessage,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Futuristic decorative log box
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(0.95.dp, CryptoCyan.copy(alpha = 0.62f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(6.dp).background(CryptoGreen, CircleShape).align(Alignment.CenterVertically))
                Text(text = "RSI Relative indicators calculated", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(6.dp).background(CryptoGreen, CircleShape).align(Alignment.CenterVertically))
                Text(text = "MACD trend histograms synchronized", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(6.dp).background(CryptoCyan, CircleShape).align(Alignment.CenterVertically))
                Text(text = "Evaluating historical probability metrics", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            }
        }
    }
}
internal val SignalProTimeframes = listOf("6H", "2H", "4H", "12H", "24H", "3D", "7D")
internal val signalProTimeframeDurationMinutes = mapOf(
    "1M" to 1,
    "5M" to 5,
    "15M" to 15,
    "30M" to 30,
    "45M" to 45,
    "1H" to 60,
    "2H" to 120,
    "4H" to 240,
    "6H" to 360,
    "12H" to 720,
    "24H" to 1440,
    "3D" to 4320,
    "7D" to 10080
)
internal fun signalProTimeframeLabel(index: Int): String = SignalProTimeframes.getOrElse(index) { "7D" }
internal fun signalProForecastHours(index: Int): Int = when (signalProTimeframeLabel(index)) {
    "2H" -> 2
    "4H" -> 4
    "6H" -> 6
    "12H" -> 12
    "24H" -> 24
    "3D" -> 72
    else -> 168
}
internal fun signalProSpotConfidence(coin: SpotSignal, index: Int): Int = when (signalProTimeframeLabel(index)) {
    "2H" -> (coin.confidencePct + 2).coerceIn(60, 99)
    "4H" -> (coin.confidencePct + 1).coerceIn(60, 99)
    "6H" -> coin.confidencePct
    "12H" -> coin.confidenceTwelveHoursPct ?: coin.confidencePct
    "24H" -> (coin.confidencePct - 5).coerceIn(60, 99)
    "3D" -> (coin.confidencePct - 10).coerceIn(52, 99)
    else -> (coin.confidencePct - 16).coerceIn(45, 99)
}
internal fun signalProSpotPriceDiffLabel(index: Int): String = "PRICE ${signalProTimeframeLabel(index)} AGO"
internal fun signalProSpotScaleFactor(index: Int): Double = when (signalProTimeframeLabel(index)) {
    "2H" -> 0.45
    "4H" -> 0.70
    "6H" -> 1.0
    "12H" -> 1.8
    "24H" -> 3.2
    "3D" -> 7.5
    else -> 12.0
}
internal fun signalProSpotGrowthPotential(coin: SpotSignal, index: Int): Double = when (signalProTimeframeLabel(index)) {
    "2H" -> coin.growthPotentialPct * 0.45
    "4H" -> coin.growthPotentialPct * 0.70
    "6H" -> coin.growthPotentialPct
    "12H" -> coin.growthPotentialTwelveHoursPct ?: (coin.growthPotentialPct * 1.5)
    "24H" -> coin.growthPotentialPct * 2.2
    "3D" -> coin.growthPotentialPct * 3.5
    else -> coin.growthPotentialPct * 5.0
}
internal fun signalProTargetLabel(index: Int): String = "PREDICTED"
internal fun signalProFuturesProbability(coin: FuturesSignal, index: Int): Int = when (signalProTimeframeLabel(index)) {
    "2H" -> (coin.probabilityPct + 2).coerceIn(40, 99)
    "4H" -> (coin.probabilityPct + 1).coerceIn(40, 99)
    "6H" -> coin.probabilityPct
    "12H" -> coin.probabilityTwelveHoursPct ?: coin.probabilityPct
    "24H" -> (coin.probabilityPct - 4).coerceIn(40, 99)
    "3D" -> (coin.probabilityPct - 8).coerceIn(35, 99)
    else -> (coin.probabilityPct - 12).coerceIn(30, 99)
}
internal fun signalProFuturesPriceChangeMultiplier(index: Int): Double = when (signalProTimeframeLabel(index)) {
    "2H" -> 0.42
    "4H" -> 0.68
    "6H" -> 1.0
    "12H" -> 1.48
    "24H" -> 2.1
    "3D" -> 3.8
    else -> 5.5
}
internal fun signalProfileConfidenceColor(score: Int): Color = titanPositiveScoreColor(score)
internal fun signalProfileRiskColor(value: String): Color = titanRiskProfileColor(value)
internal fun signalProfileAllocationColor(value: String): Color = titanAllocationProfileColor(value)
internal fun signalProfileDirectionColor(value: String): Color {
    val normalized = value.uppercase()
    return when {
        normalized.contains("BEAR") || normalized.contains("SHORT") || normalized.contains("কমছে") || normalized.contains("নিম্নমুখী") -> CryptoRedText
        normalized.contains("BULL") || normalized.contains("LONG") || normalized.contains("বাড়ছে") || normalized.contains("উর্ধ্বমুখী") -> CryptoGreen
        else -> CryptoCyan
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
    val isBengali by viewModel.isBengali.collectAsState()

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
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
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
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentWidth()
            ) {
                IconButton(
                    onClick = { viewModel.runScanner() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    CryptoCyan.copy(alpha = 0.18f),
                                    Color(0xFF050A13)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(1.dp, BorderColor, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-run scanner",
                        tint = CryptoCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                SignalProLanguageSwitchButton(
                    isBengali = isBengali,
                    onClick = { viewModel.toggleLanguage() }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Condensed AI Modality and Scan Button
        val useAiOracle by viewModel.useAiOracle.collectAsState()
        val lastScanTime by viewModel.lastScanTime.collectAsState()
        
        val now = System.currentTimeMillis()
        val diffSecs = (now - lastScanTime) / 1000
        val diffMins = diffSecs / 60
        val timeString = if (diffMins > 0) "$diffMins mins ago" else "$diffSecs secs ago"
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Modality Switch Side
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x4422E6FF),
                                    Color(0x2207111D),
                                    Color(0xFF050A13)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 0.8.dp,
                            color = Color(0x6617D6FF),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x8822E6FF),
                                    Color(0x4400D9FF),
                                    Color(0x11080B11),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(
                                elevation = 14.dp,
                                shape = CircleShape,
                                clip = false
                            )
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF1B3142),
                                        Color(0xFF080B11),
                                        Color(0xFF02040A)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xCC17D6FF),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_oracle_runtime_mark),
                            contentDescription = "AI Oracle Modality Logo",
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "AI ORACLE MODALITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (useAiOracle) "Deep Gemini Sentient API" else "Fast Technical Simulator local",
                        fontSize = 9.sp,
                        color = if (useAiOracle) CryptoCyan else TextSecondary
                    )
                    Text(
                        text = "Last Scan: $timeString",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = useAiOracle,
                    onCheckedChange = { viewModel.toggleOracleMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CryptoCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurface
                    ),
                    modifier = Modifier.scale(0.7f)
                )
            }
            
            // Scan Button
            Button(
                onClick = { viewModel.runScanner() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE2E8F0), // Near white
                    contentColor = DarkBackground
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(36.dp)
                    .graphicsLayer {
                        shadowElevation = 8.dp.toPx()
                        shape = RoundedCornerShape(8.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Scan Now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Category Tab Selectors (A: Spot Trading, B: Futures Trading)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(0.95.dp, CryptoCyan.copy(alpha = 0.62f), RoundedCornerShape(12.dp))
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
                .padding(horizontal = 12.dp)
        ) {
            when (selectedIndex) {
                0 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ScrollableTimeframeRow(
                            selectedInterval = spotTimeframe,
                            onIntervalSelected = { spotTimeframe = it }
                        )

                        val sortedSpot = data.spotSignals.sortedByDescending { coin ->
                            signalProSpotConfidence(coin, spotTimeframe)
                        }.take(10)

                        SpotTradingList(
                            signals = sortedSpot,
                            timeframeIndex = spotTimeframe,
                            viewModel = viewModel
                        )
                    }
                }
                1 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
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
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        val rawFutures = if (futuresSubTab == 0) data.futuresLongSignals else data.futuresShortSignals
                        val sortedFutures = rawFutures.sortedByDescending { coin ->
                            signalProFuturesProbability(coin, futuresTimeframe)
                        }.take(10)

                        FuturesTradingList(
                            signals = sortedFutures,
                            timeframeIndex = futuresTimeframe,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun SignalProLanguageSwitchButton(
    isBengali: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkSurface,
            contentColor = CryptoCyan
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .height(36.dp)
            .width(82.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isBengali) "English" else "বাংলা",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Clip
        )
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
            .padding(vertical = 8.dp),
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
            .padding(vertical = 8.dp),
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
fun SpotTradingList(signals: List<SpotSignal>, timeframeIndex: Int, viewModel: CryptoViewModel) {
    val oraclePick = signals.maxByOrNull { it.opportunityScore }
    val livePrices by viewModel.livePrices.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp)
    ) {
        if (oraclePick != null) {
            item {
                OraclePickCard(asset = oraclePick, timeframeIndex = timeframeIndex, viewModel = viewModel, livePrices = livePrices)
            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
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
            SpotItemCard(coin, timeframeIndex = timeframeIndex, viewModel = viewModel, livePrices = livePrices)
        }
    }
}
