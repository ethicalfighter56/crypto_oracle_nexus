# Titan Crypto Oracle Nexus Bengali Translation Glossary

Project-level Bengali translation source of truth.

Owner override rule:
Manually selected simple Bengali wording must be preserved.
Use simple Bengali that normal traders can understand.

Implementation rule:
Do not translate every English trading term. Current project display style must be preserved. Only replace labels that exactly match this glossary or are clearly intended equivalents.

Keep prices, ROI, TP, SL, score, rank, coin symbols, percentages, and trading abbreviations unchanged.

Core Translation Table

English| Bengali
VALIDITY WINDOW| বৈধতার নির্দিষ্ট মেয়াদ
AI ORACLE ANALYTICS COGNITION| এআই ওরাকলের বিশ্লেষণমূলক তথ্য
AI Decision Brief| AI এর সংক্ষিপ্ত সিদ্ধান্ত
MULTI-AI CONSENSUS ENGINES| মাল্টি-এআই মডেলের ঐক্যমত ইঞ্জিন
Discrepancy| দ্বিমত
Weightage| গুরুত্বের হার
High Confidence| উচ্চ আস্থা
Verify Entry| এন্ট্রি যাচাই
Allocation| পুঁজি বণ্টন
CONSENSUS| সম্মিলিত
CONFIDENCE| আস্থা
DIRECTION| দিকনির্দেশনা
PROBABILITY| সম্ভাবনা
PROBABILITY SCORE| সম্ভাব্যতা স্কোর
RISK SCORE| ঝুঁকির স্কোর
LOW| কম
MEDIUM| মাঝারি
HIGH| বেশি
EXTREME| খুব বেশি
Trend| প্রবণতা
Trend Confirmed| বাজারের প্রবণতা নিশ্চিত
Volume Confirmed| লেনদেন নিশ্চিত
Momentum Confirmed| মতিগতির জোর নিশ্চিত
Liquidity Confirmed| বাজারের নগদ প্রবাহ নিশ্চিত
Risk Evaluated| ঝুঁকি যাচাইকৃত
PERSISTED REGIME TRACE| চলতি বাজারের মতিগতি
CURRENT MARKET REGIME| অন-চেইন বাজার পরিস্থিতি
BULLISH| উর্ধ্বমুখী প্রবণতা
BEARISH| নিম্নমুখী প্রবণতা
CORRECTION| সাময়িক মন্দা
BREAK-EVEN| লাভ-ক্ষতি সমান
SIDEWAYS| দাম স্থির ভাব
RALLY| দাম টানা বাড়ছে
VOLATILITY| বাজার অস্থির
CRASH| পতন
RECESSION| আর্থিক মন্দা
SENTIMENT| বাজার মনোভাব
ACCUMULATION| সঞ্চয় হচ্ছে
DISTRIBUTION| বিক্রির চাপ
ACTIVE DURING INSIGHT| বর্তমানে সক্রিয়
Trigger| সংকেত
Execution| সম্পাদন
Copilot| সহচালক
Momentum| মতিগতি
Volume| লেনদেন
ACCUMULATING| সঞ্চয় হচ্ছে
Leverage| গৃহীত ঋণ
Conservative| কম ঝুকি
Balanced| মধ্যম ঝুঁকি
Moderate| মধ্যম ঝুঁকি
Aggressive| বড় ঝুঁকি
BROKER| দালাল
YIELD| অর্জিত আয়
PREDICTION| অনুমান
PREDICTIVE| আনুমানিক
Breakout| সীমা পার
Reversal| মোড় ঘোরা
Consolidation| দামের জটলা
Divergence| বিপরীতমুখী
Scalping| ঝটপট ট্রেড
Exposure| ঝুঁকির পরিমাণ
Trailing Stop| স্মার্ট প্রফিট-লক
Spread| দামের পার্থক্য
Slippage| হাতছাড়া
Arbitrage| দাম-ভিন্নতার সুযোগ
Drawdown| সর্বোচ্চ ক্ষতি
Whale| বড় ব্যবসায়ী
Fakeout| ধোঁকা
Liquidation| পুঁজি-শূন্য
Margin Call| তহবিল ঘাটতি
Resistance| দামের ঊর্ধ্বসীমা
Support| দামের নিম্নসীমা

Terms To Keep Unchanged

Do not translate these unless the current UI already uses Bengali for them:

Term
LONG
SHORT
BUY LONG
SELL SHORT
SPOT
FUTURES
ENTRY
TARGET
STOP LOSS
TAKE PROFIT
ROI
PnL
TP
SL
SCORE
RANK
AI
ORACLE
RADAR
SIGNAL
SCAN
BTC / ETH / XRP / SOL / ADA and other coin symbols

Context Rules

Spot Bearish Context

If asset is bearish in Spot:

- Direction: "নিম্নমুখী প্রবণতা"
- Opportunity: "সতর্কতা / অপেক্ষা"
- Color: red or warning
- Meaning: spot buyer should be cautious.

Futures Short Context

If asset is bearish and short signal is active:

- Direction: "নিম্নমুখী প্রবণতা"
- Trade Bias: "SELL SHORT"
- Opportunity: "SHORT অনুকূল"
- Expected Gain: green positive value

This avoids confusion because bearish movement can be profitable for a valid futures short setup.

Final Label Map

BULLISH = উর্ধ্বমুখী প্রবণতা
BEARISH = নিম্নমুখী প্রবণতা

LONG FAVORABLE = LONG অনুকূল
SHORT FAVORABLE = SHORT অনুকূল

SELL SHORT = SELL SHORT
BUY LONG = BUY LONG

EXPECTED GAIN = প্রত্যাশিত লাভ
PROJECTED MOVE = সম্ভাব্য দামের গতি
TRADE EDGE = ট্রেড সুবিধা

Implementation Notes

1. Preserve current Bengali UI where it already displays acceptable simple Bengali.
2. Replace only matching English labels or clearly equivalent existing Bengali labels.
3. Do not translate trading abbreviations, coin symbols, prices, scores, ranks, ROI, TP, SL, PnL, or percentages.
4. Use compact Bengali only where the UI has enough space.
5. Use mixed English-Bengali for trading action labels such as "SHORT অনুকূল" and "LONG অনুকূল".
6. Do not use "দাম কমছে" as a replacement for "BEARISH"; use "নিম্নমুখী প্রবণতা".
7. Do not use "দাম বাড়ছে" as a replacement for "BULLISH"; use "উর্ধ্বমুখী প্রবণতা".
8. Only those articles that correspond to the current text of the project will be changed, and those that are not on this list but are displaying Bangla in the project, will remain as it is.

Project Risk/Score Color Rules

Positive Score Color:
85-100 = Green
75-84 = Gold
65-74 = Orange
0-64 = Red

Risk Score Color:
0-15 = Green / LOW
16-25 = Gold / MEDIUM
26-35 = Orange / HIGH
36-100 = Red / EXTREME

Risk Profile / Position Allocation Posture:
CONSERVATIVE = Cyan Blue
MODERATE = Green
AGGRESSIVE = Gold

Risk Score is a danger scale. Higher risk is worse.
Risk Profile is an execution posture scale, not raw danger.
