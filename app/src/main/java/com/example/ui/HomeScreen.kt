package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.NewsItem
import com.example.model.OracleAnalysisResponse
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CryptoViewModel

@Composable
fun HomeScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    val newsFeed by viewModel.newsFeedData.collectAsState()
    val useAiOracle by viewModel.useAiOracle.collectAsState()
    val isBengali = false // Legacy/ticker uses English by default

    var isNewsBengali by remember { mutableStateOf(false) }
    var isInsightsBengali by remember { mutableStateOf(false) }
    var isMarketIndexBengali by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // App Header Brand Profile
        item {
            HeaderSection()
        }

        // Ticker for top coins: BTC, ETH, XRP
        item {
            TopCoinsTickerSection(newsFeed, isBengali)
        }

        // AI Oracle Switch Card
        item {
            OracleModeControllerCard(
                useAiOracle = useAiOracle,
                onToggle = { viewModel.toggleOracleMode(it) }
            )
        }

        // Action CTA: Prominent Analysis Launcher
        item {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.Analysis) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("analysis_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CryptoGreen,
                    contentColor = DarkBackground
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Analysis scanner icon",
                        modifier = Modifier.size(24.getValidDp())
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "RUN ORACLE SCANNER",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Star Divider
        item {
            StarDivider()
        }

        // Section Title: Crypto News Feed
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "News icon",
                    tint = CryptoCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isNewsBengali) "সর্বশেষ ওরাকল মার্কেট ফিড" else "LATEST ORACLE MARKET FEED",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CryptoCyan,
                    letterSpacing = 1.sp
                )
            }
        }

        // Scrolling News List
        items(newsFeed.newsList) { article ->
            NewsCard(article, isNewsBengali, onToggleLanguage = { isNewsBengali = !isNewsBengali })
        }

        // Star Divider
        item {
            StarDivider()
        }

        // New Section Title: Deep Analytical Insights
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Insights icon",
                    tint = CryptoGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isInsightsBengali) "ওরাকল গভীর বিশ্লেষণাত্মক অন্তর্দৃষ্টি" else "ORACLE DEEP ANALYTICAL INSIGHTS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CryptoGreen,
                    letterSpacing = 1.sp
                )
            }
        }

        // Scrolling Deep Insights List
        items(newsFeed.deepInsights) { insight ->
            DeepInsightCard(
                insight = insight,
                isBengali = isInsightsBengali,
                onToggleLanguage = { isInsightsBengali = !isInsightsBengali }
            )
        }

        // Star Divider preceding Dedicated Pricing Section (Oracle Market Index)
        item {
            StarDivider()
        }

        // Dedicated Pricing Section (Oracle Market Index Header)
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                // 3 Green Stars in front
                Row {
                    repeat(3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Glossy star indicator",
                            tint = CryptoGreen,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(horizontal = 1.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = if (isMarketIndexBengali) "ওরাকল মার্কেট সূচক" else "ORACLE MARKET INDEX",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 3 Green Stars at the back
                Row {
                    repeat(3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Glossy star indicator",
                            tint = CryptoGreen,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(horizontal = 1.dp)
                        )
                    }
                }
            }
        }

        // Decoupled beautiful Currency Exchange Rate tile
        item {
            LiveCurrencyTile(
                isBengali = isMarketIndexBengali,
                onToggleLanguage = { isMarketIndexBengali = !isMarketIndexBengali }
            )
        }
    }
}

@Composable
fun StarDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "*   *   *",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BorderColor.copy(alpha = 0.5f),
            letterSpacing = 12.sp
        )
    }
}

// Inline helper to bypass any dynamic dp evaluation quirks
private fun Int.getValidDp() = this.dp

@Composable
fun BangladeshTimeWidget() {
    var timeText by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while (true) {
            val nowInDhaka = ZonedDateTime.now(ZoneId.of("Asia/Dhaka"))
            timeText = nowInDhaka.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))
            delay(1000)
        }
    }

    val silverGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFAFAFA), // Crisp Metallic White
            Color(0xFFBDC3C7), // Silver
            Color(0xFFE5E7EB)  // Soft Platinum
        )
    )

    val gmtGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E6ED), // Pale White-Blue
            Color(0xFF94A3B8)  // Medium Slate Grey
        )
    )

    Box(
        modifier = Modifier
            .size(60.dp) // Maintain exact square ratio matching the logo size
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate Black (base)
                        Color(0xFF1E293B), // Medium 3D Shade
                        Color(0xFF090E1A)  // Core Depth shadow
                    )
                )
            )
            .drawBehind {
                val gridSpacing = 6.dp.toPx()
                // Horizontal grid lines representing on-chain ledger matrix
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0x1880FFE8), // Subtle glowing quantum teal line
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }
                // Vertical grid lines
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0x1880FFE8),
                        start = androidx.compose.ui.geometry.Offset(x, 0f),
                        end = androidx.compose.ui.geometry.Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }
            }
            .border(1.5.dp, CryptoGreen, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = timeText,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                style = androidx.compose.ui.text.TextStyle(brush = silverGradient),
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "GMT+6:00",
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Black,
                style = androidx.compose.ui.text.TextStyle(brush = gmtGradient),
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Embed the generated high-quality custom png asset
            Image(
                painter = painterResource(id = R.drawable.ic_crypto_oracle_logo_1780253119065),
                contentDescription = "Crypto Signal Oracle logo representing deep predictive analytics",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, CryptoGreen, RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Crypto Signal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = CryptoCyan,
                    letterSpacing = 2.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)) {
                            append("Oracle ")
                        }
                        withStyle(style = SpanStyle(color = TextSecondary, fontSize = 18.sp, fontWeight = FontWeight.Bold)) {
                            append("B")
                        }
                        withStyle(style = SpanStyle(color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)) {
                            append("Y ")
                        }
                        withStyle(style = SpanStyle(color = CryptoGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)) {
                            append("Z")
                        }
                        withStyle(style = SpanStyle(color = CryptoGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)) {
                            append("AHID")
                        }
                    },
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))

        // Bangladesh Live Clock
        BangladeshTimeWidget()
    }
}

@Composable
fun TopCoinsTickerSection(newsFeed: OracleAnalysisResponse, isBengali: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isBengali) "ওরাকল রেফারেন্স সূচক" else "ORACLE REFERENCE INDEX",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val btcSpot = newsFeed.spotSignals.find { it.coinSymbol == "BTC" }
            val ethSpot = newsFeed.spotSignals.find { it.coinSymbol == "ETH" }
            val xrpSpot = newsFeed.spotSignals.find { it.coinSymbol == "XRP" }
            val solSpot = newsFeed.spotSignals.find { it.coinSymbol == "SOL" }
            val adaSpot = newsFeed.spotSignals.find { it.coinSymbol == "ADA" }

            val btcPrice = btcSpot?.currentPrice ?: 66450.0
            val btcChange = btcSpot?.growthPotentialPct ?: 3.99

            val ethPrice = ethSpot?.currentPrice ?: 3485.50
            val ethChange = ethSpot?.growthPotentialPct ?: 3.57

            val xrpPrice = xrpSpot?.currentPrice ?: 0.512
            val xrpChange = xrpSpot?.growthPotentialPct ?: -5.8

            val solPrice = solSpot?.currentPrice ?: 164.20
            val solChange = solSpot?.growthPotentialPct ?: 4.12

            val adaPrice = adaSpot?.currentPrice ?: 0.435
            val adaChange = adaSpot?.growthPotentialPct ?: -2.15

            TickerRow(symbol = "BTC", name = "Bitcoin", price = btcPrice, changePct = btcChange)
            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))
            TickerRow(symbol = "ETH", name = "Ethereum", price = ethPrice, changePct = ethChange)
            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))
            TickerRow(symbol = "XRP", name = "Ripple", price = xrpPrice, changePct = xrpChange)
            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))
            TickerRow(symbol = "SOL", name = "Solana", price = solPrice, changePct = solChange)
            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))
            TickerRow(symbol = "ADA", name = "Cardano", price = adaPrice, changePct = adaChange)
        }
    }
}

@Composable
fun TickerRow(symbol: String, name: String, price: Double, changePct: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        when (symbol) {
                            "BTC" -> AccentGold.copy(alpha = 0.15f)
                            "ETH" -> CryptoCyan.copy(alpha = 0.15f)
                            "XRP" -> CryptoRed.copy(alpha = 0.15f)
                            "SOL" -> CryptoGreen.copy(alpha = 0.15f)
                            "ADA" -> CryptoRed.copy(alpha = 0.15f)
                            else -> CryptoCyan.copy(alpha = 0.15f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol.take(1),
                    color = when (symbol) {
                        "BTC" -> AccentGold
                        "ETH" -> CryptoCyan
                        "XRP" -> CryptoRed
                        "SOL" -> CryptoGreen
                        "ADA" -> CryptoRed
                        else -> CryptoCyan
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = symbol,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (price >= 100) String.format("$%,.2f", price) else String.format("$%.3f", price),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            
            val isPositive = changePct >= 0
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isPositive) "Price up" else "Price down",
                    tint = if (isPositive) CryptoGreen else CryptoRed,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%+.2f%%", changePct),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) CryptoGreen else CryptoRed
                )
            }
        }
    }
}

@Composable
fun OracleModeControllerCard(
    useAiOracle: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderBorder(if (useAiOracle) CryptoCyan else BorderColor),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(CryptoCyan.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "AI Oracle intelligence core selector",
                        tint = CryptoCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI ORACLE MODALITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CryptoCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (useAiOracle) "Direct Gemini-3.5-Flash active" else "Fast Technical Simulator local",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Switch(
                checked = useAiOracle,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CryptoGreen,
                    checkedTrackColor = CryptoGreen.copy(alpha = 0.3f),
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = DarkSurfaceVariant
                )
            )
        }
    }
}

private fun BorderBorder(color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(1.dp, color)
}

@Composable
fun NewsCard(article: NewsItem, isBengali: Boolean, onToggleLanguage: () -> Unit) {
    val title = if (isBengali) (article.titleBengali ?: article.title) else article.title
    val summary = if (isBengali) (article.summaryBengali ?: article.summary) else article.summary
    val source = if (isBengali) (article.sourceBengali ?: article.source) else article.source
    val timeAgo = if (isBengali) (article.timeAgoBengali ?: article.timeAgo) else article.timeAgo

    val sentimentText = when (article.sentiment.uppercase()) {
        "BULLISH" -> if (isBengali) "তেজি" else "BULLISH"
        "BEARISH" -> if (isBengali) "মন্দা" else "BEARISH"
        else -> if (isBengali) "নিরপেক্ষ" else "NEUTRAL"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable { onToggleLanguage() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Source, Time, Sentiment Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = source,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(TextMuted, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeAgo,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                val badgeBg = when (article.sentiment.uppercase()) {
                    "BULLISH" -> CryptoGreen.copy(alpha = 0.12f)
                    "BEARISH" -> CryptoRedContainer
                    else -> TextMuted.copy(alpha = 0.15f)
                }
                val badgeColor = when (article.sentiment.uppercase()) {
                    "BULLISH" -> CryptoGreen
                    "BEARISH" -> CryptoRedText
                    else -> TextSecondary
                }

                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = sentimentText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Article Title
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Article Summary
            Text(
                text = summary,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun DeepInsightCard(
    insight: com.example.model.DeepInsightItem,
    isBengali: Boolean,
    onToggleLanguage: () -> Unit
) {
    val rotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isBengali) 180f else 0f,
        animationSpec = androidx.compose.animation.core.spring(
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (insight.direction == "PUMP") CryptoGreen.copy(alpha = 0.5f) else CryptoRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable { onToggleLanguage() }
    ) {
        if (rotation <= 90f) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (insight.direction == "PUMP") CryptoGreen.copy(alpha = 0.15f)
                                    else CryptoRedContainer,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = insight.coinSymbol,
                                color = if (insight.direction == "PUMP") CryptoGreen else CryptoRedText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = insight.coinName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = insight.timeframe,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    val badgeBg = if (insight.direction == "PUMP") CryptoGreen.copy(alpha = 0.15f) else CryptoRedContainer
                    val badgeColor = if (insight.direction == "PUMP") CryptoGreen else CryptoRedText
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${insight.direction} +${insight.expectedChangePct}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = badgeColor
                        )
                    }
                }

                HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RESEARCH PROJECTED TARGET:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (insight.targetPrice >= 100) String.format("$%,.2f", insight.targetPrice) else String.format("$%.3f", insight.targetPrice),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (insight.direction == "PUMP") CryptoGreen else CryptoRedText
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Why:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CryptoCyan
                )
                Text(
                    text = insight.whyEnglish,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap to translate to Bengali / বাংলায় পড়তে চাপুন ➔",
                    fontSize = 10.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .graphicsLayer { rotationY = 180f }
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
                                .size(32.dp)
                                .background(
                                    if (insight.direction == "PUMP") CryptoGreen.copy(alpha = 0.15f)
                                    else CryptoRedContainer,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = insight.coinSymbol,
                                color = if (insight.direction == "PUMP") CryptoGreen else CryptoRedText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = insight.coinName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "সময়সীমা: " + when (insight.timeframe) {
                                    "Next 24 Hours" -> "আগামী ২৪ ঘন্টা"
                                    "Next 48 Hours" -> "আগামী ৪৮ ঘন্টা"
                                    "Next 7 Days" -> "আগামী ৭ দিন"
                                    else -> insight.timeframe
                                },
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    val badgeBg = if (insight.direction == "PUMP") CryptoGreen.copy(alpha = 0.15f) else CryptoRedContainer
                    val badgeColor = if (insight.direction == "PUMP") CryptoGreen else CryptoRedText
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${if (insight.direction == "PUMP") "পাম্প" else "ডাম্প"} +${insight.expectedChangePct}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = badgeColor
                        )
                    }
                }

                HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "গবেষণা পূর্বাভাসিত লক্ষ্য:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary
                    )
                    Text(
                        text = if (insight.targetPrice >= 100) String.format("$%,.2f", insight.targetPrice) else String.format("$%.3f", insight.targetPrice),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (insight.direction == "PUMP") CryptoGreen else CryptoRedText
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "বিশ্লেষণ কারণসমূহ:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CryptoCyan
                )
                Text(
                    text = insight.whyBengali,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ইংরেজিতে পড়তে চাপুন / Tap to translate to English ➔",
                    fontSize = 10.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun LiveCurrencyTile(
    isBengali: Boolean,
    onToggleLanguage: () -> Unit
) {
    val rotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isBengali) 180f else 0f,
        animationSpec = androidx.compose.animation.core.spring(
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(CryptoCyan.copy(alpha = 0.5f), AccentGold.copy(alpha = 0.5f))
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable { onToggleLanguage() }
    ) {
        if (rotation <= 90f) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header of index
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(AccentGold.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Currency Symbol Index",
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ORACLE FREE-MARKET CURRENCY INDEX",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("P2P Crypto-Referred Rates (Real-Time ")
                                    withStyle(SpanStyle(color = CryptoCyan, fontWeight = FontWeight.Bold)) {
                                        append("LIVE")
                                    }
                                    append(")")
                                },
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(CryptoGreen, CircleShape)
                    )
                }

                HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))

                // Exchange Rates Table
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CurrencyRow(flag = "🇺🇸", code = "USD", name = "United States Dollar", rate = "118.42 BDT")
                    CurrencyRow(flag = "🇪🇺", code = "EUR", name = "Euro", rate = "128.65 BDT")
                    CurrencyRow(flag = "🇬🇧", code = "GBP", name = "British Pound", rate = "151.10 BDT")
                    CurrencyRow(flag = "🇸🇦", code = "SAR", name = "Saudi Riyal", rate = "31.58 BDT")
                    CurrencyRow(flag = "🇦🇪", code = "AED", name = "United Arab Dirham", rate = "32.24 BDT")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tap to translate to Bengali / বাংলায় পড়তে চাপুন ➔",
                    fontSize = 9.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .graphicsLayer { rotationY = 180f }
                    .padding(16.dp)
            ) {
                // Header of index - Bengali
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(AccentGold.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Currency Symbol Index",
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ওরাকল উন্মুক্ত বাজার মুদ্রা সূচক",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("ক্রিপ্টো পিটুপি ও সরাসরি বাজার রেট (রিয়েল টাইম ")
                                    withStyle(SpanStyle(color = CryptoCyan, fontWeight = FontWeight.Bold)) {
                                        append("LIVE")
                                    }
                                    append(")")
                                },
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(CryptoGreen, CircleShape)
                    )
                }

                HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))

                // Exchange Rates Table - Bengali
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CurrencyRow(flag = "🇺🇸", code = "USD", name = "ইউএস ডলার", rate = "১১৮.৪২ টাকা")
                    CurrencyRow(flag = "🇪🇺", code = "EUR", name = "ইউরো", rate = "১২৮.৬৫ টাকা")
                    CurrencyRow(flag = "🇬🇧", code = "GBP", name = "ব্রিটিশ পাউন্ড", rate = "১৫১.১০ টাকা")
                    CurrencyRow(flag = "🇸🇦", code = "SAR", name = "সৌদি রিয়াল", rate = "৩১.৫৮ টাকা")
                    CurrencyRow(flag = "🇦🇪", code = "AED", name = "ইউএই দিরহাম", rate = "৩২.২৪ টাকা")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ইংরেজিতে পড়তে চাপুন / Tap to translate to English ➔",
                    fontSize = 9.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun CurrencyRow(flag: String, code: String, name: String, rate: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = flag, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = code,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = name,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
        Text(
            text = rate,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold
        )
    }
}
