# TITAN ORACLE Risk/Profile Color + Bengali Glossary Applied Summary

## Modifications Made

- Added project-wide score/risk/posture helper functions in `com.example.ui.theme.Color.kt`.
- Locked Positive Score color profile:
  - 85–100 Green
  - 75–84 Gold
  - 65–74 Orange
  - 0–64 Red
- Locked Risk Score danger profile:
  - 0–15 LOW / Green
  - 16–25 MEDIUM / Gold
  - 26–35 HIGH / Orange
  - 36–100 EXTREME / Red
- Locked Risk Profile / allocation posture colors:
  - CONSERVATIVE = Cyan Blue
  - MODERATE = Green
  - AGGRESSIVE = Gold
- Updated Signal Pro confidence/probability/consensus score colors to use the shared positive-score profile.
- Updated Live Radar probability/consensus colors to use the shared positive-score profile.
- Updated Signal Pro and Live Radar Risk Profile outputs from LOW/MEDIUM/HIGH to CONSERVATIVE/MODERATE/AGGRESSIVE where applicable.
- Changed position allocation label from Balanced to Moderate in Signal Pro and Live Radar.
- Applied exact glossary-matching Bengali replacements where active labels matched the glossary intent:
  - BULLISH → উর্ধ্বমুখী প্রবণতা
  - BEARISH → নিম্নমুখী প্রবণতা
  - RISK SCORE → ঝুঁকির স্কোর
  - MULTI-AI CONSENSUS ENGINES → মাল্টি-এআই মডেলের ঐক্যমত ইঞ্জিন
  - HIGH → বেশি for Risk Score value display
- Preserved Bangla strings not covered by the custom glossary.
- Consolidated semantic duplicate red/green/cyan/gold aliases toward shared UI theme tokens where safe.
- Replaced stale semantic harsh red usages in Accuracy Center with `TitanRed`.

## Protected Areas Preserved

- `CryptoViewModel.kt`
- `GeminiService.kt`
- `AppDatabase.kt`
- `SignalEntity.kt`
- Network/API/business/trading calculation logic
- Mission backend/state logic
- AndroidManifest
- Gradle files
- Icon and launcher resources
- Package namespace, applicationId, and app label

## Verification Status

- Static Kotlin brace/parenthesis balance check passed.
- Old stale exact `0xFF34C759`, `0xFFFF3B30`, `0xFFFF3F60`, and `0xFFFF5252` semantic values were removed from Kotlin UI source outside canonical theme definitions.
- Broad Bengali translation was not applied; only glossary-aligned active labels were adjusted.
- Gradle build was not run in this sandbox because no Gradle wrapper is included and system Gradle is unavailable.
