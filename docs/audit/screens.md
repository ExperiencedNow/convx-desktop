# Screen & Navigation Flow Map (Phase 1 Audit)

Extracted from `app/src/main/kotlin/com/convx/music/ui/screens/NavigationBuilder.kt`.

## Navigation Graph Flowchart

```mermaid
graph TD
    Root[App Startup / MainActivity] --> Home[Screens.Home / 'home']
    Root --> Songs[Screens.Songs / 'songs' - Local Only]
    Root --> Library[Screens.Library / 'library']
    Root --> Settings[Screens.Settings / 'settings']

    Home --> Search[search/{query}]
    Home --> Album[album/{albumId}]
    Home --> Artist[artist/{artistId}]
    Home --> OnlinePlaylist[online_playlist/{playlistId}]
    Home --> Mood[mood_and_genres]
    Home --> Charts[charts_screen]
    Home --> NewRelease[new_release]

    Library --> LocalMusic[local_music]
    Library --> LocalFolder[local_folder/{path}]
    Library --> LocalPlaylist[local_playlist/{playlistId}]
    Library --> AutoPlaylist[auto_playlist/{playlist}]
    Library --> CachePlaylist[cache_playlist/{playlist}]
    Library --> TopPlaylist[top_playlist/{top}]
    Library --> History[history]
    Library --> Stats[stats]

    Artist --> ArtistSongs[artist/{artistId}/songs]
    Artist --> ArtistAlbums[artist/{artistId}/albums]
    Artist --> ArtistItems[artist/{artistId}/items]

    Settings --> Appearance[settings/appearance]
    Settings --> Content[settings/content]
    Settings --> Player[settings/player]
    Settings --> Storage[settings/storage]
    Settings --> Privacy[settings/privacy]
    Settings --> Integrations[settings/integrations]
    Settings --> About[settings/about]
    Settings --> Update[settings/update]
    Settings --> Modules[settings/modules]

    Appearance --> LiquidGlass[settings/appearance/liquidglass]
    Appearance --> ThemeSettings[settings/appearance/theme]
    Appearance --> FontSettings[settings/appearance/font]
    Appearance --> DiyEditor[settings/appearance/diy]
    Appearance --> CanvasSettings[settings/appearance/canvas]
    Appearance --> Presets[settings/appearance/presets]

    Integrations --> Discord[settings/integrations/discord]
    Integrations --> LastFM[settings/integrations/lastfm]
    Integrations --> ListenTogether[settings/integrations/listen_together]

    MiniPlayer[Mini Player Docked Accessory] -->|Morph Grow| PlayerSheet[Full Player V2 Sheet]
    PlayerSheet -->|Morph Shrink| MiniPlayer
```

## Route Table

| Route | Composable Screen | ViewModel | Entry Points | Back Behavior | Transition In / Out | Tablet / Wide Adaptation |
|---|---|---|---|---|---|---|
| `home` | `HomeScreen` | `HomeViewModel` | Bottom nav tab / Rail | Exit app if root | Shared container morph / Parallax | Adaptive grid (multi-column) |
| `songs` | `LocalSongsScreen` | `LibrarySongsViewModel` | Nav tab in local-only mode | Pop | Shared container morph / Parallax | Fast-scroll alphabet rail |
| `library` | `LibraryScreen` | `LibraryViewModel` | Bottom nav tab / Rail | Pop | Shared container morph / Parallax | Split list / grid view |
| `settings` | `SettingsScreen` | `SettingsViewModel` | Bottom nav tab / Rail | Pop | Shared container morph / Parallax | Wide settings layout |
| `search/{query}` | `OnlineSearchResult` | `SearchViewModel` | Inline search bar submit | Pop to search input | `fadeIn` / `slideOutHorizontally` | Multi-column search grid |
| `album/{albumId}` | `AlbumScreen` | `AlbumViewModel` | Tile click on Home / Artist | Pop | Hero container morph | Hero header with side-by-side tracks |
| `artist/{artistId}` | `ArtistScreen` | `ArtistViewModel` | Tile click on Home / Search / Album | Pop | Hero container morph | Hero header with discography carousels |
| `artist/{artistId}/songs` | `ArtistSongsScreen` | `ArtistSongsViewModel` | Artist "See All Songs" | Pop | Parallax push | Wide tracks table |
| `artist/{artistId}/albums` | `ArtistAlbumsScreen` | `ArtistAlbumsViewModel` | Artist "See All Albums" | Pop | Parallax push | Multi-column albums grid |
| `online_playlist/{playlistId}` | `OnlinePlaylistScreen` | `OnlinePlaylistViewModel` | Home tile / Search | Pop | Container morph | Hero header + tracks list |
| `local_playlist/{playlistId}` | `LocalPlaylistScreen` | `LocalPlaylistViewModel` | Library playlists | Pop | Container morph | Tracks list with reorder handle |
| `auto_playlist/{playlist}` | `AutoPlaylistScreen` | `AutoPlaylistViewModel` | Library auto tiles | Pop | Container morph | Tracks list |
| `cache_playlist/{playlist}` | `CachePlaylistScreen` | `CachePlaylistViewModel` | Library cached / downloads | Pop | Container morph | Offline indicator badges |
| `top_playlist/{top}` | `TopPlaylistScreen` | `TopPlaylistViewModel` | Library top music | Pop | Container morph | Ranked track numbers |
| `mood_and_genres` | `MoodAndGenresScreen` | `MoodAndGenresViewModel` | Home speed dial | Pop | Parallax push | Grid of genre chips |
| `charts_screen` | `ChartsScreen` | `ChartsViewModel` | Home speed dial | Pop | Parallax push | Top charts carousels |
| `new_release` | `NewReleaseScreen` | `NewReleaseViewModel` | Home speed dial | Pop | Parallax push | New release tiles |
| `local_music` | `LocalMusicScreen` | `LocalMusicViewModel` | Library local menu | Pop | Parallax push | Tabs for Songs/Albums/Artists |
| `local_folder/{path}` | `LocalFolderScreen` | `LocalFolderViewModel` | Local file browser | Pop up directory | Parallax push | Folder tree navigation |
| `history` | `HistoryScreen` | `HistoryViewModel` | Library history row | Pop | Parallax push | Chronological track groupings |
| `stats` | `StatsScreen` | `StatsViewModel` | Library listening stats | Pop | Parallax push | Listening charts & cards |
| `settings/appearance/liquidglass` | `GlassEffectSettings` | `SettingsViewModel` | Appearance settings | Pop | Parallax push | Live interactive glass preview sandbox |
| `settings/appearance/diy` | `DiyEditorScreen` | `DiyViewModel` | Appearance settings | Pop | Full screen sheet | Drag-and-drop sticker canvas |
| `settings/appearance/presets` | `PresetsScreen` | `PresetsViewModel` | Appearance settings | Pop | Parallax push | Preset cards with share/import/export |
| `settings/equalizer` | `AxionEqScreen` | `EqualizerViewModel` | Player / Settings | Pop | Dialog / Sheet | Graphic EQ sliders + preset chips |
| `ambient_mode` | `AmbientModeScreen` | `AmbientViewModel` | Player action menu | Pop | Crossfade | Full-screen dynamic visuals & clock |
| `recognition` | `RecognitionScreen` | `RecognitionViewModel` | Search audio icon | Pop | Scale/Fade | Listening ripple animation |
