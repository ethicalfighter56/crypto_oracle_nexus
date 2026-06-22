# TITAN ORACLE UI Decoration Source Cleanup Report

## Scope

This pass was applied directly to the uploaded `tiran_crypto_oracle_nexus-test-2.zip` snapshot.

The goal was to clean duplicate UI visual/decorative source values while preserving the currently accepted visible UI and avoiding backend/business logic changes.

## Active UI source model found

- `MainActivity.kt` still routes through legacy `com.example.ui.*` wrappers.
- The legacy wrappers delegate to feature implementations:
  - `com.example.ui.HomeScreen` -> `feature.oracle_feed.OracleFeedScreen`
  - `com.example.ui.AnalysisScreen` -> `feature.signal_pro.SignalProScreen`
  - `com.example.ui.MarketRadarScreen` -> `feature.live_radar.LiveRadarScreen`
  - `com.example.ui.MissionCenterScreen` -> `feature.mission_center.MissionCenterScreen`
  - `com.example.ui.AccuracyCenterScreen` -> `feature.accuracy_center.AccuracyCenterScreen`
- Because the wrappers are active navigation compatibility paths, they were preserved.

## Modifications made

1. Added a shared visual token source:

`app/src/main/java/com/example/ui/theme/TitanUiVisualTokens.kt`

2. Consolidated duplicate active visual decoration colors into shared tokens without changing values:

- `Color(0xFF02050D)` -> `TitanUiVisualTokens.TerminalVoid`
- `Color(0xFF050A13)` -> `TitanUiVisualTokens.TerminalPanel`
- `Color(0xFF030712)` -> `TitanUiVisualTokens.TerminalDeepPanel`
- `Color(0xFF050812)` -> `TitanUiVisualTokens.TerminalInnerPanel`
- `Color(0xFF0B1220)` -> `TitanUiVisualTokens.TerminalBluePanel`
- `Color(0xFF03111B)` -> `TitanUiVisualTokens.TerminalCyanPanel`
- `Color(0xFF04111A)` -> `TitanUiVisualTokens.TerminalBlueBlack`
- `Color(0xFFF4F8FF)` -> `TitanUiVisualTokens.TerminalBrightText`
- `Color(0xFFD1D5DB)` -> `TitanUiVisualTokens.TerminalSoftText`
- `Color(0xFFFF3F60)` -> `TitanUiVisualTokens.AccuracyLossRed`
- `Color(0xFFFF5252)` -> `TitanUiVisualTokens.DestructiveActionRed`
- `Color(0xFF9D65FF)` -> `TitanUiVisualTokens.PurpleMetric`
- `Color(0xFFFF9F0A)` -> `TitanUiVisualTokens.WarningOrange`

3. Replaced stale semantic harsh red occurrences:

- `Color(0xFFFF3B30)` -> `CryptoRedText`

4. Consolidated repeated module red/green/cyan/gold aliases where safe:

- Live Radar semantic green/red aliases now route through `CryptoGreen` / `CryptoRedText`.
- Mission Center semantic green/red/cyan/gold aliases now route through shared theme tokens.
- Oracle Feed semantic green/red/cyan/gold aliases now route through shared theme tokens.

5. Removed stale root-level previous patch/report artifacts:

- `TITAN_ORACLE_LIVE_RADAR_LABEL_CONTRAST_PATCH.md`
- `TITAN_ORACLE_LIVE_RADAR_SIGNAL_PRO_UI_APPLIED_SUMMARY.md`
- `TITAN_ORACLE_LIVE_RADAR_SIGNAL_PRO_UI_NEXT_PHASE.md`
- `TITAN_ORACLE_LIVE_RADAR_SIGNAL_PRO_UI_REPAIR_REPORT.md`
- `TITAN_ORACLE_LIVE_RADAR_STICKY_SCROLL_REPAIR_REPORT.md`
- `TITAN_ORACLE_PDF_UI_TARGETED_REPAIR_REPORT.md`
- `TITAN_ORACLE_REFACTOR_REPORT.md`
- `TITAN_ORACLE_SIGNAL_PRO_COMPACT_UI_REPAIR_REPORT.md`
- `TITAN_ORACLE_TIMEFRAME_COLOR_RADAR_UPGRADE_REPORT.md`
- `TITAN_ORACLE_VISUAL_PARITY_REPAIR_REPORT.md`

## Files changed

- `app/src/main/java/com/example/feature/accuracy_center/AccuracyCenterComponentsPart1.kt`
- `app/src/main/java/com/example/feature/accuracy_center/AccuracyCenterScreen.kt`
- `app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart1.kt`
- `app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart2.kt`
- `app/src/main/java/com/example/feature/mission_center/MissionCenterComponentsPart1.kt`
- `app/src/main/java/com/example/feature/oracle_feed/OracleFeedScreen.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart1.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart2.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart3.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart4.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart5.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart6.kt`
- `app/src/main/java/com/example/ui/theme/TitanUiVisualTokens.kt`
- `TITAN_ORACLE_UI_DECORATION_SOURCE_CLEANUP_REPORT.md`

## Protected areas intentionally not changed

- `CryptoViewModel.kt`
- `GeminiService.kt`
- `AppDatabase.kt`
- `SignalEntity.kt`
- network/API files
- trading/signal/radar calculation logic
- mission state/backend logic
- icon PNGs/resources
- launcher/adaptive resources
- AndroidManifest
- Gradle files
- package namespace
- applicationId
- app label

## Behavior / visual preservation

All shared token replacements keep the exact same ARGB values except stale `0xFFFF3B30`, which was aligned to the accepted current red source-of-truth `CryptoRedText` / `#F6465D`.

No spacing, layout, typography size, card sizing, navigation, expand/collapse behavior, trading logic, AI logic, database logic, or network logic was intentionally modified.

## Remaining manual review

- Repeated shape, padding, typography, and tile-composable patterns still exist. They are active visual code and should be extracted module-by-module only after screenshot parity checks.
- Legacy `com.example.ui.*` wrapper files are active navigation compatibility wrappers and should not be deleted until `MainActivity.kt` imports feature screens directly in a separate controlled patch.
- Bengali glossary replacement remains separate.

## Verification

- Static source rewrite completed.
- Kotlin files were checked for balanced braces and parentheses using a static pass.
- Build was not run because this ZIP does not include an executable Gradle wrapper in the sandbox.
