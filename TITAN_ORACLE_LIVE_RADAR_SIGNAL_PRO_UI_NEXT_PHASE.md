# Modifications Still Needed Next

1. Run the real Android build externally:

```bash
./gradlew assembleDebug
```

2. Smoke-test Live Radar:
   - AI engine score tiles are shorter but readable.
   - `SHORT-TERM SCALP ORACLE` and timeframe chips remain visible while scrolling.
   - Top 3 / Top 10 chevron expansion still works.
   - Expanded card details do not collapse from random inner taps.
   - Scroll position does not jump down to Real-Time Alerts Feed.

3. Smoke-test Signal Pro:
   - Futures row labels show `ENTRY / CURRENT / EXPECTED / PREDICTED`.
   - Values remain aligned in collapsed and expanded cards.
   - Spot/Futures switching and expanded matrix behavior remain unchanged.

4. Smoke-test Bengali mode:
   - Live Radar labels render as `কনসেনসাস / দিক / রিস্ক`.
   - Signal Pro labels render as `সম্মিলিত আস্থা / দিকনির্দেশনা / রিস্ক প্রোফাইল`.

5. If any visual issue remains, tune only padding, width, or alignment.
   - Do not change business logic.
   - Do not change icon resources.
   - Do not change trading/signal/radar semantics.
