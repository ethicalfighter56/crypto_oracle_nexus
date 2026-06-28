# CODEX TASK: RUNTIME UX STABILIZATION 001

## Branch

`pre-backend-test-codex-runtime-ux-001`

## Task Objective

Stabilize runtime UX before backend work. The goal is targeted UI/runtime behavior correction only, not product redesign.

## Approved Tasks

- 13: Leverage `X` suffix and numeric-only input.
- 21: App scroll/runtime performance stabilization.
- 22: Live Radar expansion parity with Signal Pro.
- 23: Live Radar smooth expansion animation.
- 24: Live Radar no jumpy screen behavior.
- 26: Signal Insight bottom buttons visible in normal content flow.
- 27: Decision Brief spacing and TITAN Insight transition speed adjustment.

## Reference Only

- 25: Signal Pro expansion behavior is the reference standard. Do not treat it as a separate change.

## UI Restrictions

- Do not fully redesign UI.
- Do not change TITAN brand identity.
- Do not change Price Matrix tile information.
- Do not change Price Matrix color mechanism.
- Compact Signal Insight only below Price Matrix where needed.

## Expected Implementation Direction

### Leverage

- Keep stored/displayed value like `3X`, `5X`, `10X`.
- During active edit, show only digits.
- On focus loss / confirm, restore `X` suffix.
- Filter input with digits only.
- Do not allow `GPT`, `3GPT`, `X3`, blank, negative, or decimal as final accepted leverage.

### Live Radar

- Use one expanded item key/state per section where possible.
- Use smooth animation such as `animateContentSize` / `AnimatedVisibility`.
- Avoid abrupt instant expansion.
- Avoid screen jumping when another tile opens.
- Avoid unnecessary continuous animation.

### Signal Insight

- Keep BACK and ACCEPT SIGNAL in scroll content flow.
- Ensure bottom padding is enough for navigation bars/device displays.
- Replace emoji-style arrows with clean arrows: `← BACK`, `ACCEPT SIGNAL →`.
- Slightly compact text below Price Matrix if needed.

### TITAN Insight / Decision Brief

- Reduce TITAN Insight button transition speed to feel aligned with VERIFY ENTRY.
- Adjust Decision Brief button spacing only slightly.

## Completion Criteria

- Only approved files changed.
- `git diff --check` passes.
- No backend/API/database/execution file changed.
- Build attempted if requested by owner.
- Summary clearly states what changed and what did not change.
