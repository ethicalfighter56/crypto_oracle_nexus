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


# 1) Add page-level language state to PredictionDashboard
pd_start = text.find("fun PredictionDashboard(")
pd_body = text.find("    Column(modifier = Modifier.fillMaxSize())", pd_start)

if pd_start != -1 and pd_body != -1:
    pd_header = text[pd_start:pd_body]
    if "val isBengali by viewModel.isBengali.collectAsState()" not in pd_header:
        anchors = [
            "    var futuresTimeframe by remember { mutableStateOf(0) }\n",
            "    var timeframe by remember { mutableStateOf(0) }\n",
            "    var isFutures by remember { mutableStateOf(false) }\n",
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


# 2) Replace toolbar right actions
# This exact marker exists in the current branch. If it changes, the fallback warns but does not break the script.
toolbar_replacement = '''            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SignalProLanguageSwitchButton(
                    isBengali = isBengali,
                    onClick = { viewModel.toggleLanguage() }
                )

                IconButton(
                    onClick = { viewModel.runScanner() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, BorderColor, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-run scanner",
                        tint = CryptoCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }'''

toolbar_marker = "            // Re-run scanner\n"
toolbar_end_marker = "\n        }\n\n        Spacer(modifier = Modifier.height(8.dp))"
toolbar_start = text.find(toolbar_marker)
toolbar_end = text.find(toolbar_end_marker, toolbar_start)

if toolbar_start != -1 and toolbar_end != -1:
    text = text[:toolbar_start] + toolbar_replacement + text[toolbar_end:]
else:
    print("WARNING: Exact toolbar marker not found. Trying fallback refresh replacement.")
    refresh_regex = re.compile(
        r"IconButton\s*\(\s*onClick\s*=\s*\{\s*viewModel\.runScanner\(\)\s*\}.*?Icons\.Default\.Refresh.*?\n\s*\}\s*\n\s*\}",
        re.DOTALL
    )
    text, replaced_count = refresh_regex.subn(toolbar_replacement, text, count=1)
    if replaced_count == 0:
        print("WARNING: Refresh button block not found. Toolbar language button may not be inserted.")


# 3) Add SignalProLanguageSwitchButton
if "fun SignalProLanguageSwitchButton(" not in text:
    insert_point = text.find("\n@Composable\nfun TabButton(")
    if insert_point == -1:
        insert_point = text.find("\n@Composable\nfun SignalTypeTabButton(")
    if insert_point == -1:
        insert_point = text.find("\n@Composable\nfun RealTimeCountdown(")

    if insert_point == -1:
        raise SystemExit("ERROR: Could not find insertion point for SignalProLanguageSwitchButton.")

    button_code = '''
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
        border = BorderStroke(1.dp, CryptoCyan.copy(alpha = 0.60f)),
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isBengali) "English" else "বাংলা",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

'''
    text = text[:insert_point] + "\n" + button_code + text[insert_point:]


# 4) Replace RealTimeCountdown with final approved visual design
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

    val titleText = if (isBengali) "বৈধতার নির্দিষ্ট মেয়াদ" else "VALIDITY WINDOW"
    val remainingText = if (isBengali) "বাকি সময়" else "Remaining"
    val windowText = if (isBengali) "উইন্ডো ${totalDurationHours}H" else "Window ${totalDurationHours}H"
    val activeText = if (isBengali) "${(progress * 100).toInt()}% সক্রিয়" else "${(progress * 100).toInt()}% active"

    val stateText = when {
        isExpired -> if (isBengali) "সিগন্যালের মেয়াদ শেষ" else "Expired"
        isUrgent -> if (isBengali) "শেষ পর্যায় • দ্রুত যাচাই করুন" else "Critical window • Review quickly"
        isCaution -> if (isBengali) "সতর্ক পর্যায় • নজর রাখুন" else "Caution window • Monitor closely"
        else -> if (isBengali) "সিগন্যাল সক্রিয় • ঝুঁকির সময় চলছে" else "Signal active • Risk window open"
    }

    val stateMeaning = when {
        isExpired -> if (isBengali) "সিগন্যালের নির্দিষ্ট সময় শেষ" else "Signal window closed"
        isUrgent -> if (isBengali) "শেষ পর্যায়ের সিগন্যাল — আগে যাচাই করুন" else "Late-stage signal — verify before action"
        isCaution -> if (isBengali) "দেরি করলে সিগন্যালের মান কমতে পারে" else "Delay may reduce signal quality"
        else -> if (isBengali) "সিগন্যাল সক্রিয় — এখনো তাড়াহুড়া নেই" else "Active window — no urgency yet"
    }

    val titleColor = Color(0xFFEAF2FF)
    val subtitleColor = Color(0xFFBFC7D6)
    val supportColor = Color(0xFF98A2B3)
    val trackColor = Color(0xFF242B3A)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF141824),
                        Color(0xFF171C2A),
                        Color(0xFF10131D)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.58f),
                shape = RoundedCornerShape(16.dp)
            )
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
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = titleColor,
                        letterSpacing = if (isBengali) 0.sp else 1.1.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    Text(
                        text = stateText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = subtitleColor,
                        modifier = Modifier.padding(top = 1.dp),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
                        maxLines = 1
                    )

                    Text(
                        text = remainingText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = supportColor,
                        maxLines = 1
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
                    .background(Color(0xFF101827).copy(alpha = 0.78f))
                    .border(
                        1.dp,
                        accentColor.copy(alpha = 0.30f),
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
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 12.sp,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
                    maxLines = 1
                )

                Text(
                    text = activeText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    maxLines = 1
                )
            }
        }
    }
}'''

text = replace_composable_function(text, "RealTimeCountdown", countdown_code)


# 5) Pass global language state to countdown calls
text = text.replace("RealTimeCountdown(coin.coinSymbol, hours, isBengali)", "RealTimeCountdown(coin.coinSymbol, hours, isBengali)")
text = text.replace("RealTimeCountdown(symbol, hours, isBengali)", "RealTimeCountdown(symbol, hours, isBengali)")
text = text.replace("RealTimeCountdown(coin.coinSymbol, hours)", "RealTimeCountdown(coin.coinSymbol, hours, isBengali)")
text = text.replace("RealTimeCountdown(symbol, hours)", "RealTimeCountdown(symbol, hours, isBengali)")


# 6) Remove old per-card / lower AI Oracle language toggle
for phrase in [
    'text = if (isBengali) "Show EN" else "বাংলায় দেখুন',
    'text = if (isBengali) "Show EN" else "বাংলায় দেখুন ➔"',
    'onClick = onToggleLanguage',
]:
    old_text = text
    text = remove_textbutton_containing(text, phrase)
    if text != old_text:
        print("Removed old language toggle containing:", phrase)


# 7) Apply approved Bengali terms in Signal Pro
text = text.replace(
    'text = "AI ORACLE ANALYTIC COGNITION",',
    'text = if (isBengali) "এআই ওরাকলের বিশ্লেষণমূলক তথ্য" else "AI ORACLE ANALYTIC COGNITION",'
)
text = text.replace(
    'text = "AI ORACLE ANALYTICS COGNITION",',
    'text = if (isBengali) "এআই ওরাকলের বিশ্লেষণমূলক তথ্য" else "AI ORACLE ANALYTICS COGNITION",'
)
text = text.replace(
    'text = if (isBengali) "এআই ওরাকল অ্যানালিটিক কগনিশন" else "AI ORACLE ANALYTIC COGNITION",',
    'text = if (isBengali) "এআই ওরাকলের বিশ্লেষণমূলক তথ্য" else "AI ORACLE ANALYTIC COGNITION",'
)


# 8) Multi-AI Consensus module: add language param + translated labels
text = text.replace(
    "fun MultiAiConsensusModule(coinSymbol: String, oracleScore: Int, isLong: Boolean)",
    "fun MultiAiConsensusModule(coinSymbol: String, oracleScore: Int, isLong: Boolean, isBengali: Boolean = false)"
)

text = text.replace(
    "MultiAiConsensusModule(coin.coinSymbol, coin.oracleScore, true)",
    "MultiAiConsensusModule(coin.coinSymbol, coin.oracleScore, true, isBengali)"
)
text = text.replace(
    "MultiAiConsensusModule(coin.coinSymbol, coin.oracleScore, isLong)",
    "MultiAiConsensusModule(coin.coinSymbol, coin.oracleScore, isLong, isBengali)"
)

old_call_80 = '''MultiAiConsensusModule(symbol, when(asset) {
                        is SpotSignal -> asset.oracleScore
                        is FuturesSignal -> asset.oracleScore
                        else -> 80
                    }, isLong)'''
new_call_80 = '''MultiAiConsensusModule(symbol, when(asset) {
                        is SpotSignal -> asset.oracleScore
                        is FuturesSignal -> asset.oracleScore
                        else -> 80
                    }, isLong, isBengali)'''
text = text.replace(old_call_80, new_call_80)

old_call_75 = '''MultiAiConsensusModule(symbol, when(asset) {
                        is SpotSignal -> asset.oracleScore
                        is FuturesSignal -> asset.oracleScore
                        else -> 75
                    }, isLong)'''
new_call_75 = '''MultiAiConsensusModule(symbol, when(asset) {
                        is SpotSignal -> asset.oracleScore
                        is FuturesSignal -> asset.oracleScore
                        else -> 75
                    }, isLong, isBengali)'''
text = text.replace(old_call_75, new_call_75)

text = text.replace(
    'text = "MULTI-AI CONSENSUS ENGINES",',
    'text = if (isBengali) "মাল্টি-এআই ঐকমত্য ইঞ্জিন" else "MULTI-AI CONSENSUS ENGINES",'
)

text = text.replace(
    'Text(text = "CONSENSUS CONFIDENCE", fontSize = 9.sp, color = TextSecondary)',
    'Text(text = if (isBengali) "ঐকমত্যের আস্থা" else "CONSENSUS CONFIDENCE", fontSize = 9.sp, color = TextSecondary)'
)

text = text.replace(
    'Text(text = "DIRECTION", fontSize = 9.sp, color = TextSecondary)',
    'Text(text = if (isBengali) "দিক" else "DIRECTION", fontSize = 9.sp, color = TextSecondary)'
)

text = text.replace(
    'Text(text = "RISK PROFILE", fontSize = 9.sp, color = TextSecondary)',
    'Text(text = if (isBengali) "ঝুঁকির ধরন" else "RISK PROFILE", fontSize = 9.sp, color = TextSecondary)'
)

text = text.replace(
    'text = consensus.direction,',
    'text = if (isBengali) { when (consensus.direction.uppercase()) { "BULLISH" -> "দাম বাড়ছে"; "BEARISH" -> "দাম কমছে"; "SIDEWAYS" -> "দাম স্থির"; else -> consensus.direction } } else consensus.direction,'
)

text = text.replace(
    'text = consensus.riskScore,',
    'text = if (isBengali) { when (consensus.riskScore.uppercase()) { "LOW" -> "কম"; "MEDIUM" -> "মাঝারি"; "HIGH" -> "তীব্র"; else -> "তীব্র" } } else consensus.riskScore,'
)


# 9) Project-wide approved Bengali wording consistency in this file
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
}

for old, new in replacements.items():
    text = text.replace(old, new)

text = text.replace(
    'when (consensus.riskScore) { "LOW" -> "কম"; "MEDIUM" -> "মাঝারি"; else -> "উচ্চ" }',
    'when (consensus.riskScore.uppercase()) { "LOW" -> "কম"; "MEDIUM" -> "মাঝারি"; "HIGH" -> "তীব্র"; else -> "তীব্র" }'
)
text = text.replace(
    'when (consensus.riskScore) { "LOW" -> "কম"; "MEDIUM" -> "মাঝারি"; else -> "তীব্র" }',
    'when (consensus.riskScore.uppercase()) { "LOW" -> "কম"; "MEDIUM" -> "মাঝারি"; "HIGH" -> "তীব্র"; else -> "তীব্র" }'
)

target.write_text(text, encoding="utf-8")


# 10) Create project-wide glossary doc
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

print("OK: Complete Signal Pro update applied.")
