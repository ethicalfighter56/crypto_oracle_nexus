# TITAN ORACLE Codex Execution Rules

Project: TITAN CRYPTO ORACLE NEXUS / TITAN ORACLE
Branch for this task: `pre-backend-test-codex-runtime-ux-001`
Base branch: `pre-backend-test`

Codex is a controlled executor only. It must not behave as an autonomous product designer, product owner, refactor agent, or backend architect.

## Mandatory Mode Rule

Codex must operate in exactly one of these modes:

1. `MODE: AUDIT ONLY`
   - Read and inspect only.
   - Do not edit files.
   - Do not write files.
   - Do not commit.
   - Do not create a PR.
   - Output only the requested plan.

2. `MODE: APPLY ONLY`
   - Apply only the approved plan.
   - Modify only files listed under `Allowed Files`.
   - Do not modify any other file.
   - Do not commit unless explicitly instructed.
   - Do not use `git add -A`.

If the prompt does not explicitly say `MODE: APPLY ONLY`, Codex must not edit files.

## Protected Areas

Never modify:

- `GeminiService.kt`
- `CryptoViewModel.kt`
- `AppDatabase.kt`
- `SignalEntity.kt`
- `CryptoData.kt`
- DAO files
- Repository files
- Room/database schema files
- Binance/API/network files
- Gradle files
- `AndroidManifest.xml`
- signing/secrets/API-key files
- real trading execution files
- CI/secrets/deployment files unless explicitly instructed

## Global Development Rules

- Do not remove working features.
- Do not simplify unless explicitly requested.
- Do not change UI identity unless explicitly approved.
- Do not touch unrelated screens.
- Do not invent requirements.
- Preserve backend, API, Room, database, Binance integration, and real execution logic.
- Keep simulation and live execution clearly separated.
- Final trading decisions remain user-owned.

## Runtime UX Task Scope

Current approved task group:

- 13: Leverage `X` suffix behavior and numeric-only leverage input.
- 21: Runtime scroll/performance stabilization after dense cockpit additions.
- 22: Live Radar signal tile expand/collapse must match Signal Pro behavior.
- 23: Live Radar expansion needs smooth animation.
- 24: Live Radar must avoid jumpy screen behavior when another tile is tapped.
- 26: Signal Insight bottom buttons must remain in normal content flow and fully visible on device displays.
- 27: Decision Brief button spacing and TITAN Insight transition speed adjustment.

Task 25 is a reference only: Signal Pro expansion behavior is the reference standard. Do not treat it as a separate task.

## Allowed Files for Runtime UX Task

Only these files may be modified:

- `app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart1.kt`
- `app/src/main/java/com/example/feature/live_radar/LiveRadarScreen.kt`
- `app/src/main/java/com/example/feature/mission_center/MissionCenterComponentsPart3.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart3.kt`
- `app/src/main/java/com/example/feature/signal_pro/SignalProMockupScreen.kt`

## Required Commands Before Editing

```bash
git status
git branch --show-current
git diff --name-only
```

If current branch is not `pre-backend-test-codex-runtime-ux-001`, stop.

## Required Commands After Editing

```bash
git diff --name-only
git diff --stat
git diff --check
```

If `git diff --name-only` shows any file outside the approved file list, revert the extra file immediately.

## Staging Rule

Do not use:

```bash
git add -A
```

Stage files individually only:

```bash
git add app/src/main/java/com/example/feature/live_radar/LiveRadarComponentsPart1.kt
git add app/src/main/java/com/example/feature/live_radar/LiveRadarScreen.kt
git add app/src/main/java/com/example/feature/mission_center/MissionCenterComponentsPart3.kt
git add app/src/main/java/com/example/feature/signal_pro/SignalProComponentsPart3.kt
git add app/src/main/java/com/example/feature/signal_pro/SignalProMockupScreen.kt
```

## Output Format

After AUDIT ONLY, output:

1. Current branch
2. Current git status
3. Files that need modification
4. Exact modification plan
5. Files that must not be touched
6. Risk/stability note
7. Verification commands

After APPLY ONLY, output:

1. Modified files
2. Diff summary
3. Verification result
4. Any build errors if tested
5. Items intentionally left unchanged
