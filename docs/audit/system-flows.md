# System Flows & Architecture (Phase 1 Audit)

Detailed sequence diagrams for critical subsystems of Convx r52.

## 1. Cold Start → Home Loaded

```mermaid
sequenceDiagram
    autonumber
    participant App as App / MainActivity
    participant DS as DataStore / Preferences
    participant DB as Room Database
    participant VM as HomeViewModel
    participant IT as InnerTube Client
    participant UI as HomeScreen UI

    App->>DS: Read settings (Theme, GlassConfig, DynamicTheme, etc.)
    App->>DB: Initialize Room DB & DAOs
    App->>VM: Initialize HomeViewModel
    VM->>DB: Query cached shelves & recent history
    DB-->>VM: Emit cached home sections
    VM-->>UI: Display cached home content immediately (no flicker)
    VM->>IT: Request HomeFeed (`YouTube.home()`)
    IT-->>VM: Parse InnerTube SectionListRenderer
    VM->>DB: Persist fresh shelves to DB cache
    VM-->>UI: Smoothly update Home shelves via animated content
```

## 2. Tap a Song → Client Chain → Stream URL → Player UI

```mermaid
sequenceDiagram
    autonumber
    participant UI as Track Card / List Row
    participant Q as PlaybackQueue
    participant MS as MusicService / PlayerEngine
    participant YT as YTPlayerUtils
    participant IT as InnerTube
    participant MP as MiniPlayer / Player V2

    UI->>Q: Play item (MediaMetadata, SongId)
    Q->>MS: Set current media item
    MS-->>MP: Update UI state (Title, Artist, Artwork, Buffering)
    MS->>YT: Resolve playback stream (SongId)
    Note over YT: r52 Client Chain Order:<br/>1. VISIONOS (Apple Vision)<br/>2. IOS<br/>3. ANDROID_VR_1_43_32<br/>4. WEB_REMIX<br/>5. TVHTML5_SIMPLY_EMBEDDED
    YT->>IT: PlayerRequest(client = VISIONOS, songId)
    IT-->>YT: Stream response (formats, cipher / n-param)
    alt Cipher / n-param Present
        YT->>YT: Deobfuscate n-param via QuickJS
    end
    YT-->>MS: Audio Stream URL (Opus/WebM or AAC/MP4)
    MS->>MS: Initialize stream playback, start pre-buffering next track
    MS-->>MP: PlaybackState -> PLAYING (Playback position ticks)
```

## 3. Playback Failure → Retry → visitorData Rotation → Fallback

```mermaid
sequenceDiagram
    autonumber
    participant MS as PlayerEngine
    participant YT as YTPlayerUtils
    participant IT as InnerTube API

    MS->>YT: Play stream with Client 1 (VISIONOS)
    YT->>IT: Fetch stream
    IT-->>YT: Error 403 / "Confirm you're not a bot"
    Note over YT: Failure detected: rotate visitorData & advance chain
    YT->>YT: Rotate visitorData token
    YT->>IT: Retry with Client 2 (IOS) + rotated visitorData
    alt Success
        IT-->>YT: Valid stream URL
        YT-->>MS: Play stream
    else Further Failures
        YT->>IT: Fallback Client 3 (ANDROID_VR)
        IT-->>YT: Valid stream URL
        YT-->>MS: Play stream
    end
```

## 4. Synced Lyrics Flow

```mermaid
sequenceDiagram
    autonumber
    participant UI as LyricsSheet UI
    participant LM as LyricsManager
    participant DB as LyricsCache (Room)
    participant BL as BetterLyrics Provider
    participant LR as LRCLIB Provider
    participant KG as Kugou Provider

    UI->>LM: Request lyrics for active track (Title, Artist, Duration)
    LM->>DB: Check local DB lyrics cache
    alt Cache Hit
        DB-->>LM: Cached synced lyrics
        LM-->>UI: Word-by-word highlighted lines
    else Cache Miss
        LM->>BL: Request word-synced lyrics
        alt BetterLyrics Hit
            BL-->>LM: Word-synced timecode spans
        else Fallback
            LM->>LR: Request line-synced lyrics
            alt LRCLIB Hit
                LR-->>LM: LRC timestamps
            else Fallback
                LM->>KG: Request Kugou translated lyrics
                KG-->>LM: Kugou lyrics
            end
        end
        LM->>DB: Save lyrics to cache
        LM-->>UI: Emit parsed lyrics stream synced to track position
    end
```

## 5. Preset Export / Import Schema

Presets in Convx r52 store:
- `layout`: JSON sticker coordinates and scale
- `playerIcons`: custom control glyph slot mappings
- `glassConfig`: blur, vibrancy, lensHeight, lensAmount, surfaceTint, highlightOpacity
- File format: UTF-8 JSON bundle with version tag, keeping complete backward and forward compatibility with Android.
