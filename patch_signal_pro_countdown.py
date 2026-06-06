from pathlib import Path

path = Path("app/src/main/java/com/example/ui/AnalysisScreen.kt")

if not path.exists():
    raise SystemExit("ERROR: AnalysisScreen.kt not found.")

text = path.read_text(encoding="utf-8")

signature = "@Composable\nfun RealTimeCountdown(coinSymbol: String, totalDurationHours: Int) {"
start = text.find(signature)

if start == -1:
    raise SystemExit("ERROR: RealTimeCountdown function not found.")

brace_start = text.find("{", start)
if brace_start == -1:
    raise SystemExit("ERROR: Function opening brace not found.")

depth = 0
end = None

for i in range(brace_start, len(text)):
    ch = text[i]
    if ch == "{":
        depth += 1
    elif ch == "}":
        depth -= 1
        if depth == 0:
            end = i + 1
            break

if end is None:
    raise SystemExit("ERROR: RealTimeCountdown function end not found.")

replacement = '''@Composable
fun RealTimeCountdown(coinSymbol: String, totalDurationHours: Int) {
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

    val stateColor = when {
        isExpired -> CryptoRed
        isUrgent -> CryptoRedText
        isCaution -> AccentGold
        else -> CryptoCyan
    }

    val stateText = when {
        isExpired -> "Expired"
        isUrgent -> "Critical window • Review quickly"
        isCaution -> "Caution window • Monitor closely"
        else -> "Signal active • Risk window open"
    }

    val pulseTransition = rememberInfiniteTransition(label = "ValidityPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = if (isUrgent) 0.45f else 0.18f,
        targetValue = if (isUrgent) 0.95f else 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ValidityPulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        stateColor.copy(alpha = if (isUrgent) pulseAlpha else 0.16f),
                        DarkSurfaceVariant,
                        DarkSurface
                    )
                )
            )
            .border(
                width = 1.dp,
                color = stateColor.copy(alpha = if (isUrgent) 0.75f else 0.38f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VALIDITY WINDOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = stateText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = stateColor.copy(alpha = 0.92f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isExpired) "EXPIRED" else timeText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = stateColor,
                        maxLines = 1
                    )

                    Text(
                        text = if (isExpired) "No remaining window" else "Remaining",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = stateColor,
                trackColor = BorderColor.copy(alpha = 0.65f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Window ${totalDurationHours}H",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Text(
                    text = "${(progress * 100).toInt()}% active",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = stateColor
                )
            }
        }
    }
}'''

new_text = text[:start] + replacement + text[end:]

if new_text == text:
    print("No change needed.")
else:
    path.write_text(new_text, encoding="utf-8")
    print("OK: Replaced RealTimeCountdown() in AnalysisScreen.kt")
