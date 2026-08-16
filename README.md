# FateCaster

**Cast fate. Read the outcome. Keep the record.**

FateCaster is a small, local-first d20 resolution app for Android.

It rolls a d20, applies a modifier, compares the result against a target number, resolves the degree of outcome, and preserves recent cast history.

FateCaster is also **CaveCode Reference Implementation 001** — the first application being designed, built, reviewed, handed off, released, and maintained using CaveCode as a human/AI project protocol.

---

## What FateCaster Does

Set two values:

- **Modifier:** -10 through +10
- **Target Number:** 1 through 30

Then tap:

**CAST FATE**

FateCaster produces one authoritative d20 result and resolves it as:

- **CRITICAL SUCCESS**
- **SUCCESS**
- **FAILURE**
- **CRITICAL FAILURE**

Natural rolls have priority:

- Natural 20 → **CRITICAL SUCCESS**
- Natural 1 → **CRITICAL FAILURE**

The arithmetic is still preserved even when a natural roll overrides the outcome.

Example:

```text
Raw d20: 20
Modifier: -5
Final: 15
TN: 20

CRITICAL SUCCESS
NATURAL 20
```

---

## Resolution Rules

```text
raw d20 + modifier = final value
```

When no natural-roll override applies:

```text
10 or more above TN  → CRITICAL SUCCESS
Meet or beat TN      → SUCCESS
Below TN             → FAILURE
10 or more below TN  → CRITICAL FAILURE
```

---

## Two Screens

FateCaster v1 intentionally has only two primary screens.

### CAST

The main operational screen.

```text
Choose Modifier
      ↓
Choose TN
      ↓
CAST FATE
      ↓
Awaiting your Fate…
      ↓
Result
```

The large d20 displays the actual raw roll.

The modified final value and resolved outcome are shown separately.

### HISTORY

A read-only local record of the most recent 100 casts.

Each record preserves:

- date and time
- raw d20 result
- modifier
- final value
- target number
- outcome
- natural 1 / natural 20 status

Newest casts appear first.

---

## Local First

FateCaster's core operation requires:

- no account
- no login
- no backend
- no cloud database
- no AI service
- no network connection
- no unnecessary Android permissions

---

## Android Baseline

FateCaster v1 is being built with:

- **Kotlin**
- **Jetpack Compose**
- **Single-Activity architecture**
- **Pure Kotlin resolution engine**
- **Local history storage**

Application ID:

```text
ai.voidwalker.fatecaster
```

Minimum Android SDK:

```text
API 23
```

Initial development version:

```text
0.1.0
```

---

## Architecture

The authoritative dice rules do not live in the UI.

```text
CAST UI
   ↓
Application State
   ↓
RollResolver
   ↓
RollResult
   ↓
Local History
```

The UI presents the result.

It does not decide the result.

---

## Broken Frontier

FateCaster began as the dice-resolution mechanism for **Broken Frontier**.

The Android application is intentionally being built as a standalone product first.

Broken Frontier integration is future work.

FateCaster v1 does not depend on Broken Frontier.

---

## CaveCode Reference Implementation 001

FateCaster is being used to test CaveCode through a complete software lifecycle:

1. design
2. implementation
3. revision
4. testing
5. interruption
6. resumption
7. Android packaging
8. Google Play release
9. later maintenance

CaveCode does not build FateCaster.

It preserves the project truth, settled decisions, constraints, handoff state, and exact point where work should resume.

The read-first project map is:

```text
FATECASTER.cavecode.txt
```

---

## Project Status

**Implementation-ready**

Current implementation order:

```text
1. Android project skeleton
2. Pure Kotlin RollResolver
3. Resolver tests
4. Local history storage
5. CAST screen
6. HISTORY screen
7. Full integration
8. First complete local app test
```

---

## Philosophy

FateCaster is intentionally small.

The goal is to build one understandable application correctly, release it for real, learn the Android release lifecycle, and preserve enough project truth that another human or AI can continue the work without reconstructing it from old conversations.

---

## License

No license has been granted at this time.

Public source availability does not imply permission to copy, modify, redistribute, or reuse the software.
