from pathlib import Path
import re

target = Path("app/src/main/java/com/example/ui/AnalysisScreen.kt")
if not target.exists():
    raise SystemExit("ERROR: AnalysisScreen.kt not found.")

text = target.read_text(encoding="utf-8")


def replace_composable_function(source: str, function_name: str, replacement: str) -> str:
    pattern = re.compile(r"@Composable\s+fun\s+" + re.escape(function_name) + r"\s*\(")
    match = pattern.search(source)
    if not match:
        raise SystemExit(f"ERROR: Composable function not found: {function_name}")

    start = match.start()
    brace_start = source.find("{", match.end())
    if brace_start == -1:
        raise SystemExit(f"ERROR: Opening brace not found for: {function_name}")

    depth = 0
    end = None

    for i in range(brace_start, len(source)):
        if source[i] == "{":
            depth += 1
        elif source[i] == "}":
            depth -= 1
            if depth == 0:
                end = i + 1
                break

    if end is None:
        raise SystemExit(f"ERROR: Function end not found for: {function_name}")

    return source[:start] + replacement + source[end:]


def replace_balanced_row_containing(source: str, scope_start: int, scope_end: int, anchor: str, replacement: str) -> str:
    anchor_pos = source.find(anchor, scope_start, scope_end)
    if anchor_pos == -1:
        print(f"WARNING: Toolbar anchor not found: {anchor}")
        return source

    row_start = source.rfind("Row(", scope_start, anchor_pos)
    if row_start == -1:
        print("WARNING: Toolbar Row start not found.")
        return source

    line_start = source.rfind("\n", 0, row_start)
    line_start = 0 if line_start == -1 else line_start + 1

    brace_start = source.find("{", row_start)
    if brace_start == -1:
        print("WARNING: Toolbar Row opening brace not found.")
        return source

    depth = 0
    end = None

    for i in range(brace_start, len(source)):
        if source[i] == "{":
            depth += 1
        elif source[i] == "}":
            depth -= 1
            if depth == 0:
                end = i + 1
                break

    if end is None:
        print("WARNING: Toolbar Row end not found.")
        return source

    return source[:line_start] + replacement + source[end:]


def remove_textbutton_containing(source: str, phrase: str) -> str:
    pos = source.find(phrase)
    if pos == -1:
        return source

    start = source.rfind("TextButton(", 0, pos)
    if start == -1:
        start = source.rfind("Button(", 0, pos)
    if start == -1:
        return source

    line_start = source.rfind("\n", 0, start)
    line_start = 0 if line_start == -1 else line_start + 1

    brace_start = source.find("{", start)
    if brace_start == -1:
        return source

    depth = 0
    end = None

    for i in range(brace_start, len(source)):
        if source[i] == "{":
            depth += 1
        elif source[i] == "}":
            depth -= 1
            if depth == 0:
                end = i + 1
                break

    if end is None:
        return source

    if end < len(source) and source[end] == "\n":
        end += 1

    return source[:line_start] + source[end:]


# ------------------------------------------------------------
# 1) Ensure Signal Pro page-level global language state exists
# ------------------------------------------------------------
pd_start = text.find("fun PredictionDashboard(")
pd_body = text.find("    Column(modifier = Modifier.fillMaxSize())", pd_start)

if pd_start != -1 and pd_body != -1:
    pd_header = text[pd_start:pd_body]

    if "val isBengali by viewModel.isBengali.collectAsState()" not in pd_header:
        anchors = [
            "    var futuresTimeframe by remember { mutableStateOf(0) }\n",
            "    var spotTimeframe by remember { mutableStateOf(0) }\n",
            "    var futuresSubTab by remember { mutableStateOf(0) }\n",
        ]

        inserted = False

        for anchor in anchors:
            if anchor in pd_header:
                text = text.replace(
                    anchor,
                    anchor + "    val isBengali by viewModel.isBengali.collectAsState()\n",
                    1
                )
                inserted = True
                break

        if not inserted:
            text = text[:pd_body] + "    val isBengali by viewModel.isBengali.collectAsState()\n" + text[pd_body:]


# ------------------------------------------------------------
# 2) Toolbar: reload/rescan button left, language switch far right
# ------------------------------------------------------------
pd_start = text.find("fun PredictionDashboard(")
toolbar_scope_end = text.find("        Spacer(modifier = Modifier.height(8.dp))", pd_start)
if toolbar_scope_end == -1:
    toolbar_scope_end = text.find("        // Condensed AI Modality and Scan Button", pd_start)

toolbar_replacement = '''            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                    CryptoCyan.copy(alpha = 0.22f),
                                    Color(0xFF050A13)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(1.dp, CryptoCyan.copy(alpha = 0.68f), CircleShape)
                        .graphicsLayer {
                            shadowElevation = 8.dp.toPx()
                            shape = CircleShape
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-run scanner",
                        tint = CryptoCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                SignalProLanguageSwitchButton(
                    isBengali = isBengali,
                    onClick = { viewModel.toggleLanguage() }
                )
            }'''

text = replace_balanced_row_containing(
    source=text,
    scope_start=pd_start,
    scope_end=toolbar_scope_end,
    anchor="SignalProLanguageSwitchButton(",
    replacement=toolbar_replacement
)


# ------------------------------------------------------------
# 3) Language button: crystal glow
# ------------------------------------------------------------
language_button_code = '''@Composable
fun SignalProLanguageSwitchButton(
    isBengali: Boolean,
    onClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "LanguageButtonGlow")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LanguageGlowAlpha"
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF050A13),
            contentColor = CryptoCyan
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CryptoCyan.copy(alpha = glowAlpha)),
        modifier = Modifier
            .height(32.dp)
            .wrapContentWidth()
            .graphicsLayer {
                shadowElevation = 12.dp.toPx()
                shape = RoundedCornerShape(8.dp)
            },
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isBengali) "English" else "বাংলা",
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            color = CryptoCyan,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = CryptoCyan.copy(alpha = 0.80f),
                    offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                    blurRadius = 8f
                )
            )
        )
    }
}'''

if "fun SignalProLanguageSwitchButton(" in text:
    text = replace_composable_function(text, "SignalProLanguageSwitchButton", language_button_code)
else:
    insert_point = text.find("\n@Composable\nfun TabButton(")
    if insert_point == -1:
        insert_point = text.find("\n@Composable\nfun RealTimeCountdown(")
    if insert_point == -1:
        raise SystemExit("ERROR: Could not find insertion point for SignalProLanguageSwitchButton.")
    text = text[:insert_point] + "\n" + language_button_code + "\n\n" + text[insert_point:]


# ------------------------------------------------------------
# 4) Validity Window: animated crystal motion gradient
# ------------------------------------------------------------
countdown_code = '''@Composable
fun RealTimeCountdown(
    coinSymbol: String,
    totalDurationHours: Int,
    isBengali: Boolean = false
) {
    val totalSeconds = (totalDurationHours * 3600).coerceAtLeast(1)

    var remainingSeconds by remember(coinSymbol, totalDurationHours) {
        val safeRange = (totalSeconds - 900).coerceAtLeast(600)
        val stableOffsetSeconds = (coinSymbol.hashCode().absoluteValue % safeRange) + 300
        mutableStateOf(stableOffsetSeconds.coerceIn(0, totalSeconds))
    }

    LaunchedEffect(coinSymbol, totalDurationHours) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }

    val sweepTransition = rememberInfiniteTransition(label = "ValidityGradientSweep")
    val sweepX by sweepTransition.animateFloat(
        initialValue = -850f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ValiditySweepX"
    )

    val pulseAlpha by sweepTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.66f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ValidityPulseAlpha"
    )

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    val progress = (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    val isExpired = remainingSeconds <= 0
    val isUrgent = progress <= 0.20f && !isExpired
    val isCaution = progress in 0.21f..0.50f

    val accentColor = when {
        isExpired -> CryptoRedText
        isUrgent -> Color(0xFFFF6F86)
        isCaution -> AccentGold
        else -> CryptoCyan
    }

    val baseLeft = when {
        isExpired -> Color(0xFF1A0610)
        isUrgent -> Color(0xFF1E0712)
        isCaution -> Color(0xFF1A1304)
        else -> Color(0xFF03141D)
    }

    val titleText = if (isBengali) "বৈধতার নির্দিষ্ট মেয়াদ" else "VALIDITY WINDOW"
    val remainingText = if (isBengali) "বাকি সময়" else "Remaining"
    val windowText = "Window ${totalDurationHours}H"
    val activeText = "${(progress * 100).toInt()}% active"

    val stateText = when {
        isExpired -> if (isBengali) "সিগন্যালের মেয়াদ শেষ" else "Expired"
        isUrgent -> if (isBengali) "শেষ পর্যায় • দ্রুত যাচাই করুন" else "Critical window • Review quickly"
        isCaution -> if (isBengali) "সতর্ক পর্যায় • নজর রাখুন" else "Caution window • Monitor closely"
        else -> if (isBengali) "সিগন্যাল সক্রিয় • ঝুঁকির সময় চলছে" else "Signal active • Risk window open"
    }

    val stateMeaning = when {
        isExpired -> if (isBengali) "সিগন্যালের সময় শেষ" else "Signal window closed"
        isUrgent -> if (isBengali) "শেষ পর্যায় — আগে যাচাই করুন" else "Late-stage signal — Verify before action"
        isCaution -> if (isBengali) "দেরি করলে মান কমতে পারে" else "Delay may reduce signal quality"
        else -> if (isBengali) "সক্রিয় — এখনো তাড়াহুড়া নেই" else "Active window — no urgency yet"
    }

    val titleColor = if (isUrgent || isExpired) Color(0xFFFF91A6) else Color(0xFFF4FAFF)
    val subtitleColor = if (isUrgent || isExpired) Color(0xFFFFB7C4) else Color(0xFFD6F5FF)
    val supportColor = Color(0xFFB7C2D4)
    val trackColor = Color(0xFF111A28)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        baseLeft,
                        Color(0xFF090F1C),
                        Color(0xFF02050D)
                    )
                )
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        accentColor.copy(alpha = pulseAlpha),
                        Color.White.copy(alpha = 0.12f),
                        accentColor.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    startX = sweepX,
                    endX = sweepX + 620f
                )
            )
            .border(
                width = 1.25.dp,
                color = accentColor.copy(alpha = 0.88f),
                shape = RoundedCornerShape(17.dp)
            )
            .graphicsLayer {
                shadowElevation = 16.dp.toPx()
                shape = RoundedCornerShape(17.dp)
            }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titleText,
                        fontSize = if (isBengali) 12.sp else 9.sp,
                        fontWeight = FontWeight.Black,
                        color = titleColor,
                        letterSpacing = if (isBengali) 0.sp else 1.1.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = accentColor.copy(alpha = 0.82f),
                                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                                blurRadius = 10f
                            )
                        )
                    )

                    Text(
                        text = stateText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtitleColor,
                        modifier = Modifier.padding(top = 1.dp),
                        maxLines = 1,
                        softWrap = false,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = accentColor.copy(alpha = 0.70f),
                                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                                blurRadius = 8f
                            )
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isExpired) {
                            if (isBengali) "শেষ" else "EXPIRED"
                        } else {
                            timeText
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor,
                        maxLines = 1,
                        softWrap = false,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = accentColor.copy(alpha = 0.95f),
                                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                                blurRadius = 12f
                            )
                        )
                    )

                    Text(
                        text = remainingText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = supportColor,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = accentColor,
                trackColor = trackColor
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF02050D),
                                Color(0xFF0D1422),
                                Color(0xFF02050D)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        accentColor.copy(alpha = 0.50f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stateMeaning,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFF7FBFF),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = accentColor.copy(alpha = 0.70f),
                            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                            blurRadius = 8f
                        )
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = windowText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = supportColor,
                    maxLines = 1,
                    softWrap = false
                )

                Text(
                    text = activeText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    maxLines = 1,
                    softWrap = false,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = accentColor.copy(alpha = 0.66f),
                            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                            blurRadius = 7f
                        )
                    )
                )
            }
        }
    }
}'''

text = replace_composable_function(text, "RealTimeCountdown", countdown_code)


# ------------------------------------------------------------
# 5) Ensure countdown calls receive language state
# ------------------------------------------------------------
text = text.replace("RealTimeCountdown(coin.coinSymbol, hours, isBengali)", "RealTimeCountdown(coin.coinSymbol, hours, isBengali)")
text = text.replace("RealTimeCountdown(symbol, hours, isBengali)", "RealTimeCountdown(symbol, hours, isBengali)")
text = text.replace("RealTimeCountdown(coin.coinSymbol, hours)", "RealTimeCountdown(coin.coinSymbol, hours, isBengali)")
text = text.replace("RealTimeCountdown(symbol, hours)", "RealTimeCountdown(symbol, hours, isBengali)")


# ------------------------------------------------------------
# 6) StartTradeFlow: animated recommendation tile + compact Accept Signal
# ------------------------------------------------------------
start_trade_flow_code = '''@Composable
fun StartTradeFlow(viewModel: CryptoViewModel, mission: com.example.model.Mission, livePrice: Double) {
    var step by remember { mutableStateOf(0) }
    val isBengali by viewModel.isBengali.collectAsState()

    val recommendationText = remember(mission, isBengali) {
        val highConfidence = mission.confidence >= 85
        val isLong = mission.type.uppercase() == "LONG"

        when {
            isBengali && highConfidence && isLong -> "উচ্চ আস্থা — এন্ট্রি যাচাই করুন"
            isBengali && highConfidence && !isLong -> "উচ্চ আস্থা — শর্ট যাচাই করুন"
            isBengali && !highConfidence -> "সতর্কভাবে যাচাই করুন"
            !isBengali && highConfidence && isLong -> "High confidence — verify entry"
            !isBengali && highConfidence && !isLong -> "High confidence — verify short"
            else -> "Verify setup before action"
        }
    }

    if (step == 1) {
        AlertDialog(
            onDismissRequest = { step = 0 },
            title = { Text("Start this trade?", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("You are about to activate this signal for personal tracking.", color = TextSecondary) },
            confirmButton = {
                Button(onClick = { step = 2 }, colors = ButtonDefaults.buttonColors(containerColor = CryptoGreen)) {
                    Text("Continue", fontWeight = FontWeight.Black, color = DarkBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { step = 0 }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF030712),
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    } else if (step == 2) {
        val verifiedEntryLocked = remember { livePrice }

        AlertDialog(
            onDismissRequest = { step = 0 },
            title = { Text("Confirm Trade Activation", color = CryptoCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Current Market Price:", color = TextSecondary, fontSize = 12.sp)
                    Text(String.format("%.4f USDT", verifiedEntryLocked), color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("This price will be locked as your personal mission entry price.\\nThe original signal entry remains unchanged for validation.", color = AccentGold, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.startMission(mission.copy(
                        id = java.util.UUID.randomUUID().toString(),
                        entryPrice = verifiedEntryLocked,
                        originalSignalEntry = mission.entryPrice,
                        startTime = System.currentTimeMillis()
                    ))
                    viewModel.sendLocalAlert("Mission Started", "AI intelligence system successfully started monitoring ${mission.coinSymbol}")
                    step = 0
                }, colors = ButtonDefaults.buttonColors(containerColor = CryptoGreen)) {
                    Text("Activate Mission", fontWeight = FontWeight.Black, color = DarkBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { step = 1 }) {
                    Text("Back", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF030712),
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "ButtonScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "AcceptFlowPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.48f,
        targetValue = 0.84f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val recoSweepX by infiniteTransition.animateFloat(
        initialValue = -650f,
        targetValue = 650f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RecommendationSweepX"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF02050D),
                            Color(0xFF0B1220),
                            Color(0xFF02050D)
                        )
                    )
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CryptoCyan.copy(alpha = 0.42f),
                            Color.White.copy(alpha = 0.12f),
                            CryptoGreen.copy(alpha = 0.26f),
                            Color.Transparent
                        ),
                        startX = recoSweepX,
                        endX = recoSweepX + 520f
                    )
                )
                .border(1.dp, CryptoCyan.copy(alpha = 0.66f), RoundedCornerShape(10.dp))
                .graphicsLayer {
                    shadowElevation = 12.dp.toPx()
                    shape = RoundedCornerShape(10.dp)
                }
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = recommendationText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFF4F8FF),
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 12.sp,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = CryptoCyan.copy(alpha = 0.75f),
                        offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                        blurRadius = 8f
                    )
                )
            )
        }

        Box(
            modifier = Modifier
                .scale(scale)
                .height(40.dp)
                .widthIn(min = 128.dp, max = 164.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CryptoCyan.copy(alpha = pulseAlpha),
                            CryptoGreen.copy(alpha = pulseAlpha)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(10.dp))
                .graphicsLayer {
                    shadowElevation = 12.dp.toPx()
                    shape = RoundedCornerShape(10.dp)
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = { step = 1 }
                )
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Trade",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "ACCEPT SIGNAL",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    softWrap = false,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.White.copy(alpha = 0.58f),
                            offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                            blurRadius = 7f
                        )
                    )
                )
            }
        }
    }
}'''

text = replace_composable_function(text, "StartTradeFlow", start_trade_flow_code)


# ------------------------------------------------------------
# 7) Multi-AI Consensus: sample-style strong cyan panel
# ------------------------------------------------------------
multi_ai_code = '''@Composable
fun MultiAiConsensusModule(
    coinSymbol: String,
    oracleScore: Int,
    isLong: Boolean,
    isBengali: Boolean = false
) {
    val geminiScore = (oracleScore - 4).coerceIn(60, 99)
    val gptScore = (oracleScore - 8).coerceIn(60, 99)
    val claudeScore = (oracleScore - 5).coerceIn(60, 99)
    val consensusScore = ((geminiScore + gptScore + claudeScore) / 3).coerceIn(0, 100)

    val directionText = if (isBengali) {
        if (isLong) "দাম বাড়ছে" else "দাম কমছে"
    } else {
        if (isLong) "BULLISH" else "BEARISH"
    }

    val riskText = if (isBengali) {
        if (oracleScore >= 85) "কম" else "মাঝারি"
    } else {
        if (oracleScore >= 85) "LOW" else "MEDIUM"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.35.dp, CryptoCyan.copy(alpha = 0.72f), RoundedCornerShape(14.dp))
            .graphicsLayer {
                shadowElevation = 14.dp.toPx()
                shape = RoundedCornerShape(14.dp)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF03111B),
                            Color(0xFF0B1220),
                            Color(0xFF02050D)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = if (isBengali) "মাল্টি-এআই ঐকমত্য ইঞ্জিন" else "MULTI-AI CONSENSUS ENGINES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = CryptoCyan,
                letterSpacing = if (isBengali) 0.sp else 1.2.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = CryptoCyan.copy(alpha = 0.72f),
                        offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                        blurRadius = 8f
                    )
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiScoreTile("Gemini Pro AI", geminiScore, Modifier.weight(1f))
                AiScoreTile("GPT-4o Quant", gptScore, Modifier.weight(1f))
                AiScoreTile("Claude Sentient", claudeScore, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF04111A),
                                Color(0xFF0B1824),
                                Color(0xFF04111A)
                            )
                        )
                    )
                    .border(1.1.dp, CryptoCyan.copy(alpha = 0.78f), RoundedCornerShape(10.dp))
                    .padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ConsensusMetricColumn(
                    label = if (isBengali) "ঐকমত্যের আস্থা" else "CONSENSUS CONFIDENCE",
                    value = "$consensusScore%",
                    valueColor = CryptoCyan,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(CryptoCyan.copy(alpha = 0.35f))
                )

                ConsensusMetricColumn(
                    label = if (isBengali) "দিকনির্দেশ" else "DIRECTION",
                    value = directionText,
                    valueColor = CryptoGreen,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(CryptoCyan.copy(alpha = 0.35f))
                )

                ConsensusMetricColumn(
                    label = if (isBengali) "রিস্ক প্রোফাইল" else "RISK PROFILE",
                    value = riskText,
                    valueColor = CryptoGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}'''

text = replace_composable_function(text, "MultiAiConsensusModule", multi_ai_code)


# ------------------------------------------------------------
# 8) Add / replace AiScoreTile + ConsensusMetricColumn helpers
# ------------------------------------------------------------
ai_score_tile_code = '''@Composable
fun AiScoreTile(title: String, score: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF02050D),
                        Color(0xFF080E18),
                        Color(0xFF02050D)
                    )
                )
            )
            .border(0.9.dp, CryptoCyan.copy(alpha = 0.28f), RoundedCornerShape(9.dp))
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDCE5F5),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "$score / 100",
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = CryptoGreen,
                maxLines = 1,
                softWrap = false,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = CryptoGreen.copy(alpha = 0.70f),
                        offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                        blurRadius = 7f
                    )
                )
            )
        }
    }
}'''

consensus_metric_code = '''@Composable
fun ConsensusMetricColumn(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD0D8E8),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = valueColor,
            maxLines = 1,
            softWrap = false,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = valueColor.copy(alpha = 0.70f),
                    offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                    blurRadius = 8f
                )
            )
        )
    }
}'''

if "fun AiScoreTile(" in text:
    text = replace_composable_function(text, "AiScoreTile", ai_score_tile_code)
else:
    insert_point = text.find("\n@Composable\nfun MultiAiConsensusModule(")
    text = text[:insert_point] + "\n" + ai_score_tile_code + "\n\n" + text[insert_point:]

if "fun ConsensusMetricColumn(" in text:
    text = replace_composable_function(text, "ConsensusMetricColumn", consensus_metric_code)
else:
    insert_point = text.find("\n@Composable\nfun MultiAiConsensusModule(")
    text = text[:insert_point] + "\n" + consensus_metric_code + "\n\n" + text[insert_point:]


# ------------------------------------------------------------
# 9) AI Oracle Analytics: darker crystal panel + aligned heatmap
# ------------------------------------------------------------
ai_explanation_code = '''@Composable
fun AiExplanationModule(
    whyEnglish: String,
    whyBengali: String,
    coinSymbol: String,
    isBengali: Boolean,
    onToggleLanguage: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isBengali) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isBengali) "এআই ওরাকলের বিশ্লেষণমূলক তথ্য" else "AI ORACLE ANALYTICS COGNITION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = CryptoCyan,
            letterSpacing = if (isBengali) 0.sp else 1.sp,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = CryptoCyan.copy(alpha = 0.76f),
                    offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                    blurRadius = 8f
                )
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.35.dp, CryptoCyan.copy(alpha = 0.72f), RoundedCornerShape(12.dp))
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14 * density
                    shadowElevation = 12.dp.toPx()
                    shape = RoundedCornerShape(12.dp)
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF03111B),
                                Color(0xFF0B1220),
                                Color(0xFF02050D)
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                if (rotation <= 90f) {
                    Column {
                        Text(
                            text = whyEnglish,
                            fontSize = 13.sp,
                            color = Color(0xFFF4F8FF),
                            lineHeight = 18.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = CryptoCyan.copy(alpha = 0.24f),
                                    offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    blurRadius = 5f
                                )
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "QUANTITATIVE HEATMAP SIGNALS",
                            fontSize = 9.sp,
                            color = CryptoCyan,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        HeatmapSignalsAlignedRow(
                            firstLabel = "Trend",
                            firstValue = "STRONG",
                            firstColor = CryptoGreen,
                            secondLabel = "Momentum",
                            secondValue = "HOT",
                            secondColor = AcceleratorCyanColor(coinSymbol),
                            thirdLabel = "Volume",
                            thirdValue = "ACCUMULATING",
                            thirdColor = AccentGold
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    ) {
                        Text(
                            text = whyBengali,
                            fontSize = 13.sp,
                            color = Color(0xFFF4F8FF),
                            lineHeight = 18.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = CryptoCyan.copy(alpha = 0.24f),
                                    offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    blurRadius = 5f
                                )
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "পরিমাণগত হিটম্যাপ সিগন্যাল",
                            fontSize = 9.sp,
                            color = CryptoCyan,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        HeatmapSignalsAlignedRow(
                            firstLabel = "ট্রেন্ড",
                            firstValue = "শক্তিশালী",
                            firstColor = CryptoGreen,
                            secondLabel = "মোমেন্টাম",
                            secondValue = "তীব্র",
                            secondColor = AcceleratorCyanColor(coinSymbol),
                            thirdLabel = "ভলিউম",
                            thirdValue = "সঞ্চয় হচ্ছে",
                            thirdColor = AccentGold
                        )
                    }
                }
            }
        }
    }
}'''

text = replace_composable_function(text, "AiExplanationModule", ai_explanation_code)


# ------------------------------------------------------------
# 10) Heatmap aligned row helper
# ------------------------------------------------------------
heatmap_helper = '''@Composable
fun HeatmapSignalsAlignedRow(
    firstLabel: String,
    firstValue: String,
    firstColor: Color,
    secondLabel: String,
    secondValue: String,
    secondColor: Color,
    thirdLabel: String,
    thirdValue: String,
    thirdColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            InsightMetricPill(firstLabel, firstValue, firstColor)
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            InsightMetricPill(secondLabel, secondValue, secondColor)
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            InsightMetricPill(thirdLabel, thirdValue, thirdColor)
        }
    }
}'''

if "fun HeatmapSignalsAlignedRow(" not in text:
    insert_point = text.find("\n@Composable\nfun InsightMetricPill(")
    if insert_point == -1:
        raise SystemExit("ERROR: Could not find InsightMetricPill insertion point.")
    text = text[:insert_point] + "\n" + heatmap_helper + "\n\n" + text[insert_point:]
else:
    text = replace_composable_function(text, "HeatmapSignalsAlignedRow", heatmap_helper)


# ------------------------------------------------------------
# 11) Insight metric pill: stronger border + floating value
# ------------------------------------------------------------
insight_pill_code = '''@Composable
fun InsightMetricPill(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF02050D),
                        Color(0xFF08111C),
                        Color(0xFF02050D)
                    )
                ),
                shape = RoundedCornerShape(6.dp)
            )
            .border(0.85.dp, valueColor.copy(alpha = 0.58f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            fontSize = 8.sp,
            color = Color(0xFFD3DAE8),
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        Text(
            text = value,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = valueColor,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = valueColor.copy(alpha = 0.75f),
                    offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                    blurRadius = 7f
                )
            )
        )
    }
}'''

text = replace_composable_function(text, "InsightMetricPill", insight_pill_code)


# ------------------------------------------------------------
# 12) Remove old lower/per-card language toggle
# ------------------------------------------------------------
for phrase in [
    'text = if (isBengali) "Show EN" else "বাংলায় দেখুন',
    'text = if (isBengali) "Show EN" else "বাংলায় দেখুন ➔"',
    'onClick = onToggleLanguage',
]:
    before = text
    text = remove_textbutton_containing(text, phrase)
    if text != before:
        print("Removed old language toggle containing:", phrase)


# ------------------------------------------------------------
# 13) Stronger tile hierarchy: darker surfaces + visible borders
# ------------------------------------------------------------
text = text.replace(
    "CardDefaults.cardColors(containerColor = DarkSurface)",
    "CardDefaults.cardColors(containerColor = Color(0xFF030712))"
)
text = text.replace(
    "CardDefaults.cardColors(containerColor = DarkSurfaceVariant)",
    "CardDefaults.cardColors(containerColor = Color(0xFF050A13))"
)
text = text.replace(
    "CardDefaults.cardColors(containerColor = Color(0xFF070B12))",
    "CardDefaults.cardColors(containerColor = Color(0xFF030712))"
)
text = text.replace(
    "CardDefaults.cardColors(containerColor = Color(0xFF0A0F1A))",
    "CardDefaults.cardColors(containerColor = Color(0xFF050A13))"
)

text = text.replace(
    ".border(1.dp, BorderColor, RoundedCornerShape(12.dp))",
    ".border(1.15.dp, CryptoCyan.copy(alpha = 0.56f), RoundedCornerShape(12.dp))"
)
text = text.replace(
    ".border(2.dp, BorderColor, RoundedCornerShape(12.dp))",
    ".border(1.4.dp, CryptoCyan.copy(alpha = 0.66f), RoundedCornerShape(12.dp))"
)
text = text.replace(
    ".border(1.dp, BorderColor, RoundedCornerShape(8.dp))",
    ".border(1.dp, CryptoCyan.copy(alpha = 0.42f), RoundedCornerShape(8.dp))"
)


# ------------------------------------------------------------
# 14) Approved Bengali glossary correction
# ------------------------------------------------------------
replacements = {
    "ভ্যালিডিটি উইন্ডো": "বৈধতার নির্দিষ্ট মেয়াদ",
    "বৈধতা উইন্ডো": "বৈধতার নির্দিষ্ট মেয়াদ",
    "বৈধতার সময়সীমা": "বৈধতার নির্দিষ্ট মেয়াদ",
    "বৈধতার সময়সীমা": "বৈধতার নির্দিষ্ট মেয়াদ",

    "এআই ওরাকল অ্যানালিটিক কগনিশন": "এআই ওরাকলের বিশ্লেষণমূলক তথ্য",
    "এআই ওরাকলের অ্যানালিটিক কগনিশন": "এআই ওরাকলের বিশ্লেষণমূলক তথ্য",
    "এআই ওরাকল বিশ্লেষণ": "এআই ওরাকলের বিশ্লেষণমূলক তথ্য",
    "এআই ওরাকল বিশ্লেষণাত্মক তথ্য": "এআই ওরাকলের বিশ্লেষণমূলক তথ্য",

    "কনসেনসাস কনফিডেন্স": "ঐকমত্যের আস্থা",
    "সম্মিলিত আস্থা": "ঐকমত্যের আস্থা",
    "কনসেনসাস আস্থা": "ঐকমত্যের আস্থা",

    "বুলিশ": "দাম বাড়ছে",
    "দাম বাড়ছে": "দাম বাড়ছে",
    "বেয়ারিশ": "দাম কমছে",
    "বিয়ারিশ": "দাম কমছে",
    "বেয়ারিশ": "দাম কমছে",
    "সাইডওয়েজ": "দাম স্থির",
    "সাইডওয়ে": "দাম স্থির",
    "সাইডওয়ে": "দাম স্থির",

    "অ্যাকুমুলেটিং": "সঞ্চয় হচ্ছে",
    "জমা হচ্ছে": "সঞ্চয় হচ্ছে",
    "সংগ্রহ হচ্ছে": "সঞ্চয় হচ্ছে",
    "সঞ্চয়কারী": "সঞ্চয় হচ্ছে",
    "সঞ্চয়কারী": "সঞ্চয় হচ্ছে",
}

for old, new in replacements.items():
    text = text.replace(old, new)

text = text.replace(
    'text = "AI ORACLE ANALYTIC COGNITION",',
    'text = if (isBengali) "এআই ওরাকলের বিশ্লেষণমূলক তথ্য" else "AI ORACLE ANALYTICS COGNITION",'
)
text = text.replace(
    'text = if (isBengali) "এআই ওরাকলের বিশ্লেষণমূলক তথ্য" else "AI ORACLE ANALYTIC COGNITION",',
    'text = if (isBengali) "এআই ওরাকলের বিশ্লেষণমূলক তথ্য" else "AI ORACLE ANALYTICS COGNITION",'
)

target.write_text(text, encoding="utf-8")


# ------------------------------------------------------------
# 15) Update glossary doc
# ------------------------------------------------------------
glossary = Path("docs/bengali_translation_glossary.md")
glossary.parent.mkdir(parents=True, exist_ok=True)
glossary.write_text("""# Crypto Oracle Nexus Bengali Translation Glossary

This glossary is the project-wide source of truth for Bengali translation.

Rule:
Same English term must use the same Bengali meaning everywhere.

| English | Bengali |
|---|---|
| VALIDITY WINDOW | বৈধতার নির্দিষ্ট মেয়াদ |
| AI ORACLE ANALYTICS COGNITION | এআই ওরাকলের বিশ্লেষণমূলক তথ্য |
| CONSENSUS CONFIDENCE | ঐকমত্যের আস্থা |
| LOW | কম |
| MEDIUM | মাঝারি |
| HIGH | তীব্র |
| BULLISH | দাম বাড়ছে |
| BEARISH | দাম কমছে |
| SIDEWAYS | দাম স্থির |
| ACCUMULATING | সঞ্চয় হচ্ছে |

Additional rule:
Use simple Bengali that normal traders can understand.
Keep prices, ROI, TP, SL, score, rank, coin symbols, and percentages unchanged.
""", encoding="utf-8")

print("OK: Signal Pro sample visual upgrade applied.")
