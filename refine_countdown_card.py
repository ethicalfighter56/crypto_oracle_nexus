from pathlib import Path

path = Path("app/src/main/java/com/example/ui/AnalysisScreen.kt")
text = path.read_text(encoding="utf-8")

signature = "@Composable\nfun RealTimeCountdown(coinSymbol: String, totalDurationHours: Int) {"
start = text.find(signature)

if start == -1:
    raise SystemExit("ERROR: RealTimeCountdown function not found.")

brace_start = text.find("{", start)
depth = 0
end = None

for i in range(brace_start, len(text)):
    if text[i] == "{":
        depth += 1
    elif text[i] == "}":
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

    val accentColor = when {
        isExpired -> CryptoRedText
        isUrgent -> Color(0xFFFF7A90)
        isCaution -> AccentGold
        else -> CryptoCyan
    }

    val stateText = when {
        isExpired -> "Expired"
        isUrgent -> "Critical window • Review quickly"
        isCaution -> "Caution window • Monitor closely"
        else -> "Signal active • Risk window open"
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
                color = accentColor.copy(alpha = 0.52f),
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
                        text = "VALIDITY WINDOW",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = titleColor,
                        letterSpacing = 1.15.sp
                    )

                    Text(
                        text = stateText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = subtitleColor,
                        modifier = Modifier.padding(top = 1.dp),
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isExpired) "EXPIRED" else timeText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor,
                        maxLines = 1
                    )

                    Text(
                        text = if (isExpired) "Expired" else "Remaining",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = supportColor
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Window ${totalDurationHours}H",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = supportColor
                )

                Text(
                    text = "${(progress * 100).toInt()}% active",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
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
    print("OK: Refined RealTimeCountdown() readability and compact height.")
