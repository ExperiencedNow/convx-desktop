# 03 — TEST PLAN & RETEST LOOP

Honest framing: no one can prove "zero mistakes". This plan defines **measurable exit criteria** so that remaining risk is known, listed, and small.

---

## 1. Test layers
| Layer | What | Tooling (suggested) |
|---|---|---|
| L1 Unit | InnerTube response parsing, client-chain logic & retry rules, lyric parsers (LRC / word-level formats), preset export→import round-trip, queue logic, LRU caches, DataStore key defaults | Kotlin test, JUnit5, kotest, MockK, Ktor MockEngine with recorded fixtures |
| L2 Integration | Live-API playback chain (guest), DB migrations, download→offline playback, local scan, lyrics provider fallbacks | Gradle tests tagged `@live` (run manually, not in CI) |
| L3 UI | Compose UI tests per screen (state → expected nodes), nav graph walks, keyboard/mouse flows | Compose Desktop test APIs |
| L4 Visual parity | Screenshot of desktop screen vs Android reference at the same logical size | Script that renders screens at fixed dp sizes and diffs against `reference/` |
| L5 Motion parity | Frame-by-frame comparison of key animations (timing curve, end state) | Record desktop at 60 fps; compare to `reference/motion/` |
| L6 Performance | Budgets in §4 | JFR, Compose metrics, GPU-Z/Task Manager, frame-time log |
| L7 Soak/failure | 2-hour playback, offline mid-play, expired stream URL, bot-check response, DB corruption recovery | Scripted + manual |
| L8 Platform | Win10 22H2 & Win11, DPI 100/125/150/200%, 2 monitors, high-refresh, clean VM install | Manual matrix |

## 2. Per-slice Definition of Done
A slice (docs/master §5 Phase 4) is done when:
- [ ] L1+L3 tests added and green
- [ ] Parity checklist (§3) ticked **with evidence files**
- [ ] Settings used by the slice all work (toggle on/off, persisted, restart-safe)
- [ ] No new warnings in logs at INFO+ during a 5-minute manual walk
- [ ] `PORT_MANIFEST.md` + `DEVIATIONS.md` updated
- [ ] Committed with message `slice(<n>): <name> — tests green, evidence in docs/evidence/<n>/`

## 3. Parity checklist (per screen/component)
Layout & geometry: positions, sizes, paddings, corner radii match tokens (docs/01 §C4)
Glass: blur/vibrancy/lens/tint/highlight/shadow visually match capture; backdrop attach point same
Typography: family/size/weight/color match
Color: light + dark + artwork-adaptive
Icons: same glyph, size, stroke
Motion: duration, easing/spring, interruptibility (can reverse mid-animation)
States: loading, empty, error, offline, long text, RTL/long titles, very narrow/wide window
Behavior: gestures replaced by approved desktop inputs only (F3)
Tolerances: pixel diffs allowed only from font hinting/anti-aliasing; any structural diff = defect. Record tolerance numbers in `docs/evidence/parity/README.md`.

## 4. Performance budgets (adjust after the user shares GPU/display in docs/02 §6)
- Scrolling a long list under glass: ≥ 60 fps median, 1% low ≥ 45 fps on the user's GPU
- Mini→full morph: no dropped frames on a mid-range GPU
- Idle (paused, window visible): CPU < 3%, no continuous re-recording of backdrop (r52 rule)
- Cold start to interactive Home: < 3 s on SSD (record actual)
- Memory after 1 h of use: < 1 GB (record actual); no unbounded growth (soak)
- Seek latency < 300 ms; gap between tracks < 50 ms (gapless) / configured crossfade exact

## 5. Playback test matrix (L2/L7)
| Case | Expected |
|---|---|
| Normal song, guest | Plays < 2 s after tap |
| Flagged-track (bot check) | Chain advances (VISIONOS → IOS → …), plays or shows clear error; Client probe reports per client |
| Signed-in flagged session | visitorData rotates on retry; login state untouched |
| Stream URL expiry mid-song | Silent refresh, no audible gap > 1 s |
| Network drop 30 s | Buffers, resumes automatically |
| Seek spam (50 seeks) | No crash/desync |
| Crossfade on/off, speed change | Timing correct (v1.5.2 fix) |
| Offline downloaded track | Plays without network |
| Lossless/high-quality fallback | Quality pill matches actual codec |

## 6. Retest loop (mandatory)
```
repeat:
  1. run affected L1/L3 tests
  2. run L4/L5 for affected screens
  3. run smoke suite (open app, play, seek, lyrics, search, settings toggle, restart)
  4. if any failure → fix → go to 1 (never skip to the next slice)
until: 3 consecutive passes
```
After every merge to `desktop/main`: run the **full suite** once. Each full run writes `docs/evidence/runs/<date>-<n>.md` (counts, failures, flaky tests).
Flaky tests: quarantine only with a ticket in `docs/BUGS.md`; flaky L2 `@live` tests that fail due to the unofficial API are labelled `external`, not silently ignored.

## 7. Bug triage
P0 crash/data loss/can't play/can't start · P1 feature broken or visibly off from Convx · P2 polish · P3 idea.
**Exit criteria for preview:** 0 open P0, 0 open P1, ≤ 5 P2 listed in the preview report.
**Exit criteria for .exe:** above + 3 consecutive green full-suite runs + clean-VM installer test + user approval file.

## 8. Mandatory "adversarial" checks before preview
- Fresh profile (delete app data) → first-run flow works
- Corrupt DB file → app recovers without crash
- Disconnect network at launch → Home shows offline state like Convx
- Change every Liquid Glass setting extremes (0 and max) → no crash/artifacts
- Resize window from min to max rapidly, drag between monitors with different DPI
- Run 8 hours idle with music paused → no leak (soak)
