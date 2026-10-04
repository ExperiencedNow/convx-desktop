# 04 — PREVIEW GATE & RELEASE (.exe only after your approval)

## 1. The idea
You approve **the real app, running**, not a mockup. Two preview levels:

| Level | What it is | When | How you run it |
|---|---|---|---|
| **Preview A — dev run** | The actual Compose Desktop app launched from Gradle with a "PREVIEW" badge | Anytime from Phase 3 on | `powershell -ExecutionPolicy Bypass -File scripts\preview.ps1` |
| **Preview B — portable folder** | `createDistributable` output: a runnable app folder with its own bundled runtime, **no installer, nothing installed on your PC** | At the Preview Gate (Phase 7) | `scripts\preview.ps1 -Portable` then run `Convx.exe` inside the folder it prints |

Preview B is what you judge before the real installer is made. The installer (`.exe` setup) is built **only** after you approve.

> Requirement for Preview A: JDK 21 installed (Antigravity's dev environment normally has it). Preview B needs nothing installed.

## 2. What the agent must produce for the Preview Gate (Phase 7)
`docs/evidence/preview/PREVIEW_REPORT.md` containing:
1. Build info: commit hash, pinned upstream commit (`1e2237d9`), date, JDK, engine used (e.g., libVLC), app size.
2. **Feature status table** — every Convx r52 feature: ✅ ported / 🟡 partial (what's missing) / ⛔ dropped (why) / ⏳ not yet (tier).
3. **Parity report** — side-by-side images Android vs desktop for every ★ screen in `reference/` + motion comparison notes.
4. Test results: last full-suite run, bug list by priority (docs/03 §7).
5. `DEVIATIONS.md` summary.
6. Known issues and risks (e.g., playback depends on the unofficial InnerTube API).
7. A 10-minute **guided tour script** for you (below).

## 3. Your guided tour (10 minutes) — what to try
1. Launch → Home loads; scroll fast; watch the nav bar glass over moving content.
2. Click each nav tab; watch the puck.
3. Search a song; play it; check time-to-first-sound.
4. Click the mini player: the pill should *morph* into the full player; close it again.
5. Open lyrics: word-by-word highlight and scrolling.
6. Open Up Next/queue; reorder; skip.
7. Settings → Liquid Glass: move blur/lens sliders, see live changes.
8. Resize the window small → large; sidebar should collapse/expand like Convx's tablet layout.
9. Pause 5 minutes; check CPU in Task Manager (idle should be low).
10. Close and reopen; settings and queue should persist.

Reply with: **APPROVE**, or a list of problems. For each problem, the agent opens a bug (`docs/BUGS.md`), fixes, re-runs the retest loop (docs/03 §6), and re-issues the preview.

## 4. Approval mechanism (technical gate)
The final build script refuses to run unless the repo root has `APPROVED.txt` whose entire content is:
```
APPROVED FOR EXE
```
Only **you** create that file (the agent must never create or edit it; rule F7). Optional second line: your preferred version number.

## 5. Final `.exe` build (Phase 8 — only after APPROVED.txt)
```
powershell -ExecutionPolicy Bypass -File scripts\package-exe.ps1
```
Output (expected path, verify): `desktopApp\build\compose\binaries\main\exe\Convx-<version>.exe`
Prerequisites: JDK 21 with `jpackage`; **WiX Toolset 3.x** on PATH (needed by jpackage for `.exe`). If WiX is missing, the script stops and tells you; `packageMsi` has the same requirement.

Release checklist:
- [ ] Clean Windows VM: install → launch → play → uninstall leaves no leftovers
- [ ] Upgrade install over previous version keeps settings/library
- [ ] Credits & Licenses screen shows GPL-3.0 + all credits (master §1-F8); `NOTICE` and `LICENSE` bundled
- [ ] No telemetry traffic (check with a network monitor during a 10-minute session)
- [ ] App size, startup time recorded
- [ ] SHA-256 of the installer written to `docs/evidence/release/SHA256.txt`
- [ ] Windows SmartScreen note in release text if unsigned
- [ ] Source of the exact build is published (GPL-3.0 obligation) — tag `desktop-v<version>`

## 6. Rollback
If the installer misbehaves, keep the last portable folder (Preview B) as the stable fallback. Never delete the preview artifacts before the first release is confirmed good.
