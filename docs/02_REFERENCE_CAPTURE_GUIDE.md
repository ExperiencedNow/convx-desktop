# 02 — REFERENCE CAPTURE GUIDE (the "moodboard")

**Why this exists:** you asked that the desktop app copy Convx's real look and motion, not an invented design. A moodboard I draw would be exactly the invented design you don't want. The truthful moodboard is **the real app, captured**. Put everything in the repo under `reference/` — the agent treats these files as visual ground truth (rule F1).

Time needed: about 45–60 minutes.

---

## 1. Get the app running
Option A (best): a real Android phone, **Android 13+** (the glass uses shader effects; older versions degrade `[VERIFIED-LIB]`).
Option B: Android Studio emulator, API 33+ (create one phone and one **Pixel Tablet** for the sidebar layouts). Enable hardware GPU acceleration.
Install the APK from the **nightly-r52** release assets on the Convx GitHub Releases page (`github.com/cosmictaserdev-creator/Convx/releases`, "Convx Nightly r52" → Assets). Allow "Unknown sources". Use the same build the plan targets (r52, commit `1e2237d9`).

## 2. Folder & naming
```
reference/
  screens/    NN_screen_state_theme_device.png
  motion/     NN_what_theme_device.mp4
  settings/   NN_settingspage.png
  notes.md    anything you noticed (lag, glitches, odd behavior)
```
Example: `07_player-v2_playing_dark_phone.png`, `M03_mini-to-full-morph_dark_phone.mp4`.
Capture in **dark and light** for the key screens (marked ★).

## 3. Screenshots checklist (tick as you go)
**Core**
- [ ] ★ Home (top, scrolled, with custom background image if set)
- [ ] ★ Search (empty, typing with suggestions, results)
- [ ] ★ Library (each tab/filter), fast-scroll rail visible
- [ ] ★ Playlist page (hero header), Album page, Artist page
- [ ] ★ Floating pill nav bar (all tabs; close-up on the puck)
- [ ] Inline search bar in nav bar
**Player**
- [ ] ★ Mini player (pill) — normal, with waveform seek bar on, multi-icon mode on
- [ ] ★ Player V2 (Apple Music style) — playing, paused, with lyrics, with queue (Up Next)
- [ ] Classic player (V1)
- [ ] Quality/codec pill and song-info sheet
- [ ] ★ Full-screen synced lyrics (word-by-word highlight, several lines)
- [ ] Inline lyrics
- [ ] Ambient mode
**Menus**
- [ ] Long-press overlay menu (default) and sheet style
- [ ] Share / add-to-playlist dialogs
**Settings** (screenshot every page, top to bottom)
- [ ] ★ Settings → Liquid Glass (every dial and its value)
- [ ] Appearance (App Font, Library background, App icon)
- [ ] Player (Player Theme, Player Icons, Presets, Crossfade/Auto-DJ)
- [ ] Content → Logs → Client probe
- [ ] Arrange home feed, Scan music, Local-only mode
**Tablet (emulator Pixel Tablet)**
- [ ] ★ Home with collapsible sidebar (expanded + collapsed)
- [ ] Capped-width mini player, compact player option
**Local-only mode**
- [ ] Local Home shelves, Songs tab, scan screen

## 4. Screen recordings (motion is where fidelity is won or lost)
Record at the device's native refresh rate if possible. With adb: `adb shell screenrecord --time-limit 30 /sdcard/x.mp4` then `adb pull`.
- [ ] M01 Nav puck sliding between tabs
- [ ] M02 Page transition (blurred) Home → Playlist → back
- [ ] M03 **Mini pill → Player V2 morph** (open and close, slow drag if gesture-driven)
- [ ] M04 Rubber-band overscroll (top and bottom) with slow drag and a fling
- [ ] M05 Album/artist/playlist tile morph into page
- [ ] M06 Lyrics auto-scroll + word highlight (one full line)
- [ ] M07 Long-press menu open/close
- [ ] M08 Glass over moving content (scroll the list behind the nav bar slowly)
- [ ] M09 Sidebar collapse/expand (tablet)
- [ ] M10 Track change animation, artwork color change

## 5. The glass numbers
For *Settings → Liquid Glass*, screenshot each slider position **and** type the values into `reference/notes.md`. These are defaults the agent compares with source values (docs/01 §C1).

## 6. Optional but very valuable
- Short video of any behavior you want preserved exactly ("this feels great").
- List what you dislike or what lags in r52 (known issues: mini bar icon glitches while scrolling, artwork flicker `[VERIFIED-R52]`) so the agent doesn't copy bugs.
- Your Windows target: GPU model, screen resolution/DPI, refresh rate (used for performance budgets).

## 7. What the agent does with these
1. Compares extracted tokens (docs/01 §C) to what the screenshots show; flags conflicts.
2. Uses the recordings to tune timing/spring behavior and as test baselines (docs/03 §3).
3. Produces side-by-side comparisons: `docs/evidence/parity/<screen>_android_vs_desktop.png`.

If you skip this step, tell the agent; it must then state in every parity report that visual parity was checked against source only.
