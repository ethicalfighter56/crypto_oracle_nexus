# Modifications Made

1. Used the last modified ZIP:
   - `titan_oracle_timeframe_color_radar_upgraded.zip`

2. Live Radar AI engine score tile height was reduced.
   - Numeric score text such as `94/100` was slightly reduced.
   - Label text such as `Gemini Pro AI` was not reduced.

3. Live Radar sticky scalp oracle control was added.
   - `SHORT-TERM SCALP ORACLE` remains visible while scrolling.
   - Timeframe chips remain visible while scrolling.
   - The full page was not turned into a bulky sticky top bar.

4. Live Radar accidental collapse / scroll-jump behavior was repaired.
   - Expanded cards no longer collapse from random taps inside detail content.
   - Collapse/expand now happens from the summary/header row.
   - Expanded state is kept stable with `rememberSaveable(timeframe)`.

5. Live Radar profile/risk tile styling was normalized.
   - Conservative / Low uses green-safe style.
   - Balanced / Medium uses cyan-neutral style.
   - Aggressive / High uses gold-warning style.
   - Critical / Extreme uses red-risk style.

6. Bengali labels were added/fixed.
   - Live Radar compact tiles:
     - `Consensus Confidence` → `কনসেনসাস`
     - `Direction` → `দিক`
     - `Risk Profile` → `রিস্ক`
   - Signal Pro tiles:
     - `Consensus Confidence` → `সম্মিলিত আস্থা`
     - `Direction` → `দিকনির্দেশনা`
     - `Risk Profile` → `রিস্ক প্রোফাইল`

7. Signal Pro futures metric labels were compacted and aligned.
   - `ENTRY (LOCKED)` → `ENTRY`
   - `CURRENT PRICE` → `CURRENT`
   - `EXPECTED GAIN / EXPECTED DROP` → `EXPECTED`
   - `6-H Predicted Target` and similar timeframe labels → `PREDICTED`

8. Protected areas were preserved.
   - No ViewModel change.
   - No GeminiService change.
   - No Room/database change.
   - No Binance/API/network change.
   - No signal/radar/trading calculation rewrite.
   - No icon or launcher resource change.
   - `R.drawable.ic_oracle_runtime_mark` preserved.
   - `#080B11` launcher background preserved.
   - Package namespace, applicationId, and app label preserved.
