# CODEX VERIFICATION CHECKLIST

## Required Git Verification

```bash
git status
git branch --show-current
git diff --name-only
git diff --stat
git diff --check
```

## Allowed Changed Files

The diff may contain only:

```text
app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart1.kt
app/src/main/java/com/example/feature/live_radar/LiveRadarScreen.kt
app/src/main/java/com/example/feature/mission_center/MissionCenterComponentsPart3.kt
app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart3.kt
app/src/main/java/com/example/feature/signal_pro/SignalProMockupScreen.kt
```

## Manual QA Checklist

1. Mission Center leverage input rejects text such as GPT and 3GPT.
2. During leverage editing, X disappears from the input box.
3. After editing leverage, X appears again.
4. Live Radar tile expansion is smooth.
5. Tapping another Live Radar tile collapses the previous tile cleanly.
6. Live Radar does not jump unpredictably when switching expanded tiles.
7. Signal Insight BACK and ACCEPT SIGNAL buttons are visible on small displays.
8. BACK arrow appears as left-arrow BACK.
9. ACCEPT arrow appears as ACCEPT SIGNAL right-arrow.
10. Price Matrix tile content and color mechanism are unchanged.
11. TITAN Insight animation feels closer to VERIFY ENTRY transition speed.
12. No backend/API/database/execution code changed.

## Optional Build

```bash
./gradlew assembleDebug --stacktrace --no-daemon
```
