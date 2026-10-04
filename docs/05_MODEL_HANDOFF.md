# 05 — MODEL HANDOFF (Claude Opus 5.5 High ⇄ Gemini 3.8 Flash High)

Goal: when Claude's tokens run out mid-task, Gemini continues **without re-planning, re-deciding, or redesigning**. All state lives in files, never in chat memory.

## 1. State files (create in Phase 0, keep updated every session)
| File | Content |
|---|---|
| `docs/PROGRESS.md` | Current phase, current slice, pinned commit, last green test run, **"Next 3 actions"**, blockers |
| `docs/PORT_MANIFEST.md` | Per source file: status, adaptations, tests |
| `docs/DECISIONS.md` | Every architecture/engine/library decision: context → decision → alternatives → date → model that decided |
| `docs/DEVIATIONS.md` | Every difference from Convx r52 (rule F3) |
| `docs/BUGS.md` | Open bugs with priority |
| `docs/OPEN_QUESTIONS.md` | Things only the user can answer |
`PROGRESS.md` format:
```
## Status (updated <date> by <model>)
Phase: 4 · Slice: 5 (Mini player + Player V2)
Last commit: <hash> · Last full test run: <file> (PASS/FAIL)
## Done since last handoff
- ...
## Next 3 actions
1. ...
2. ...
3. ...
## Blockers / waiting on user
- ...
```

## 2. Who does what
| Work | Preferred model |
|---|---|
| Phase 1 audit, Phase 2 spikes, architecture decisions, glass/motion fidelity, playback chain, anything touching `DECISIONS.md` | **Claude Opus 5.5 High** |
| Mechanical porting of already-decided slices, writing tests from templates, fixing listed bugs, running retest loops, docs/evidence housekeeping | **Gemini 3.8 Flash High** |
If Claude runs out during an architecture-level task, Gemini must **stop at a safe point** (green build, committed), write the situation in `PROGRESS.md`, and continue only with mechanical work.

## 3. Gemini operating rules
1. Read master plan §1 (rules F1–F8) and §2 at the start of **every** session. Re-read after every ~10 tool calls.
2. Work on **one slice** at a time; open the corresponding rows in `PORT_MANIFEST.md`.
3. Do **not**: change architecture, replace libraries, rename packages, refactor Convx code "for cleanliness", invent UI, tune animation values by feel. Copy values from `docs/audit/design-tokens.md` and source.
4. If a value/behavior isn't in the audit, **stop and log** in `OPEN_QUESTIONS.md`; don't guess.
5. Small commits (≤ 300 changed lines when possible); build and run tests before each commit.
6. Never edit `APPROVED.txt`, `LICENSE`, `reference/`, or upstream Android modules (`app/`, etc.).
7. When a test fails twice for the same reason, stop and write a bug entry with the log; don't loop.
8. Use exact commands from the docs; do not invent Gradle task names — run `./gradlew :desktopApp:tasks` and use what exists.

## 4. Session-start prompts (paste into Antigravity)
**For Claude Opus 5.5 High**
> You are continuing the Convx Desktop port. Read `CONVX_DESKTOP_IMPLEMENTATION.md` fully, then `docs/PROGRESS.md`, `docs/DECISIONS.md`, `docs/DEVIATIONS.md`. Obey rules F1–F8. Continue with "Next 3 actions". Audit/decision tasks are yours; log every decision. Before finishing, update PROGRESS.md with exact next steps so a smaller model can continue.

**For Gemini 3.8 Flash High**
> You are continuing the Convx Desktop port started by another model. Read `CONVX_DESKTOP_IMPLEMENTATION.md` sections 1, 2, 4, 5 and `docs/05_MODEL_HANDOFF.md`, then `docs/PROGRESS.md`. Do only the "Next 3 actions". Do not change architecture or invent design — copy from source and `docs/audit/*`. If anything is unclear, log it in `docs/OPEN_QUESTIONS.md` and move to the next safe task. Build + test before every commit. Update PROGRESS.md at the end.

## 5. Handoff checklist (agent performs before ending a session, especially when tokens are low)
- [ ] Working tree builds (`./gradlew :desktopApp:build`) or the broken state is explained in PROGRESS.md
- [ ] All changes committed on `desktop/main` (or a named WIP branch)
- [ ] `PORT_MANIFEST.md`, `DEVIATIONS.md`, `DECISIONS.md` current
- [ ] "Next 3 actions" are concrete (file paths, commands, expected result)
- [ ] Any half-finished spike has a note on how to resume

## 6. Quality guard when switching models
After each handoff, the incoming model runs the smoke suite (docs/03 §6 step 3) **before** new work. If red, fixing it is the first action.
Every 5 slices (or at each phase gate), have Claude (when tokens return) review Gemini's diffs against the parity checklist and the rules F1–F8, and log findings in `BUGS.md`.
