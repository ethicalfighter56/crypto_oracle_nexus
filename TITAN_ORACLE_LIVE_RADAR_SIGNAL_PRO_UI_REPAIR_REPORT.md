# TITAN ORACLE Live Radar + Signal Pro UI Repair Report

## 1. Modifications made

### Live Radar

- Reduced the vertical height of Live Radar AI engine score tiles by tightening the score line only.
  - Label text size such as `Gemini Pro AI` remains unchanged.
  - Numeric score text such as `94/100` was slightly reduced for a more compact tile.

- Added sticky/fixed behavior for the Live Radar scalp oracle control area.
  - `SHORT-TERM SCALP ORACLE` and the timeframe selector now remain visible while scrolling expanded radar content.
  - The whole Live Radar page was not converted into a bulky sticky top bar.

- Improved expanded Live Radar card collapse behavior.
  - Expanded cards now collapse only from the compact summary/header row.
  - Tapping inside expanded detail content no longer collapses the card accidentally.
  - Expanded state now uses `rememberSaveable(timeframe)` for more stable behavior across scroll recomposition and timeframe changes.

- Preserved existing Top 3 / Top 10 section expansion behavior.
  - Chevron-based expansion remains intact.
  - No large text button was added.

- Improved Live Radar profile/risk tile consistency.
  - Position allocation tiles now use profile-aligned colors:
    - Conservative / Low → green-safe style
    - Balanced / Medium → cyan-neutral style
    - Aggressive / High → gold-warning style
    - Critical / Extreme → red-risk style
  - Consensus metric tiles now use compact bordered micro-tile styling closer to Signal Pro’s institutional rhythm.

- Added Bengali label support for compact Live Radar consensus/risk tiles.
  - `Consensus Confidence` → `কনসেনসাস`
  - `Direction` → `দিক`
  - `Risk Profile` → `রিস্ক`

- Preserved dynamic confidence color behavior.
  - Consensus confidence continues to color by score range using the existing probability/confidence color logic.

### Signal Pro

- Repaired Futures signal top metric label alignment.
  - `ENTRY (LOCKED)` → `ENTRY`
  - `CURRENT PRICE` → `CURRENT`
  - `EXPECTED GAIN` / `EXPECTED DROP` → `EXPECTED`
  - timeframe-based predicted labels such as `6-H Predicted Target` → `PREDICTED`

- Updated the shared Signal Pro target label helper so spot/futures target labels no longer repeat the selected timeframe.
  - The selected tab already communicates timeframe context.
  - The UI label is now compact and stable: `PREDICTED`.

- Tightened Signal Pro consensus metric micro-tiles.
  - Consensus tiles now better match Live Radar compact metric rhythm.
  - Text size was not aggressively reduced.

- Updated Bengali labels in Signal Pro consensus tiles.
  - `Consensus Confidence` → `সম্মিলিত আস্থা`
  - `Direction` → `দিকনির্দেশনা`
  - `Risk Profile` → `রিস্ক প্রোফাইল`

## 2. Files changed

- `app/src/main/java/com/example/feature/live_radar/LiveRadarScreen.kt`
- `app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart1.kt`
- `app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart2.kt`
- `app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart3.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart1.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart4.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart5.kt`
- `TITAN_ORACLE_LIVE_RADAR_SIGNAL_PRO_UI_REPAIR_REPORT.md`

## 3. Files intentionally not touched

- `CryptoViewModel.kt`
- `GeminiService.kt`
- Room/database files
- Binance/API/network files
- trading/signal/radar calculation logic
- mission persistence architecture
- icon PNG resources
- launcher/adaptive resources
- `ic_oracle_runtime_mark.png`
- `AndroidManifest.xml`
- Gradle files
- package namespace
- applicationId
- app label

## 4. Build/check status

- Kotlin brace/parenthesis static balance scan: passed.
- Protected identity/resource scan: passed.
  - package namespace remains `com.example`.
  - applicationId remains `com.titancryptoraclenexus.app`.
  - app label remains `Titan Oracle`.
  - launcher background color remains `#080B11`.
  - `R.drawable.ic_oracle_runtime_mark` reference remains present.
- Targeted string checks: passed.
  - old futures labels such as `EXPECTED GAIN`, `EXPECTED DROP`, and `6-H Predicted` are no longer used in the Signal Pro metric labels.
- Gradle build was not executed in this sandbox because the project has no `gradlew` wrapper and no system `gradle` command is available.

## 5. Remaining risks

- `stickyHeader` uses Compose Foundation experimental API through `@OptIn(ExperimentalFoundationApi::class)`. This is safe for current Compose but should be verified in the real Android build.
- Visual parity must be confirmed on the same device density, screen size, and font scale used in the screenshots.
- The collapse-jump repair is implemented by limiting the card toggle target to the summary row, but final behavior should be smoke-tested with long expanded detail content.

## 6. Next recommended step

Run the real Android build and visual smoke test:

```bash
./gradlew assembleDebug
```

Then verify:

1. Live Radar AI engine score tiles are shorter but readable.
2. Live Radar scalp oracle header/timeframe row stays visible while scrolling.
3. Expanded Live Radar cards do not collapse from random taps inside detail content.
4. Signal Pro futures labels align as `ENTRY / CURRENT / EXPECTED / PREDICTED`.
5. Bengali labels render correctly in both Live Radar and Signal Pro.
6. No protected business logic, icon, resource, navigation, or identity behavior changed.
