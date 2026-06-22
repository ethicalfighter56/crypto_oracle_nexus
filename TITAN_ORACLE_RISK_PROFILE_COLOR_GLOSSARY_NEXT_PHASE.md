# TITAN ORACLE Risk/Profile Color + Bengali Glossary Next Phase

1. Apply the modified ZIP on `main_refactor_debug-test-2`.
2. Run external build with GitHub Actions or Termux Gradle.
3. Smoke-test:
   - Signal Pro
   - Live Radar
   - Mission Center
   - Oracle Feed
   - Accuracy Center
4. Verify:
   - Positive score colors follow 85/75/65 thresholds.
   - Risk Score colors follow 15/25/35 thresholds.
   - Risk Profile displays CONSERVATIVE / MODERATE / AGGRESSIVE.
   - Position Allocation displays Conservative / Moderate / Aggressive.
   - Bengali labels changed only where glossary matched.
5. Next separate patch: Live Radar label contrast if still needed after source cleanup.
