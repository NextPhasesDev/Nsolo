# NSOLO — Master Project Plan

**Organisation:** NextPhases ([nextphases.dev](https://nextphases.dev))
**Project:** Nsolo — Digital Adaptation of a Traditional Zambian Board Game
**Current Version:** v1.1.0
**Last Updated:** February 26, 2026
**Repository:** [github.com/NextPhasesDev/Nsolo](https://github.com/NextPhasesDev/Nsolo) (Private)

---

## Table of Contents

1. [Project Vision](#1-project-vision)
2. [Current State](#2-current-state)
3. [Architecture Overview](#3-architecture-overview)
4. [Roadmap & Phases](#4-roadmap--phases)
5. [TODO — Unified Prioritised Task List](#5-todo--unified-prioritised-task-list)
6. [Sound & Music Plan](#6-sound--music-plan)
7. [UI/UX Improvement Plan](#7-uiux-improvement-plan)
8. [Web Deployment Plan](#8-web-deployment-plan)
9. [Mobile Plan](#9-mobile-plan)
10. [Multiplayer Plan](#10-multiplayer-plan)
11. [Campaign & Story Mode Plan](#11-campaign--story-mode-plan)
12. [Portfolio & Domain Strategy](#12-portfolio--domain-strategy)
13. [Distribution & Packaging](#13-distribution--packaging)
14. [Versioning Policy](#14-versioning-policy)
15. [Git & Release Workflow](#15-git--release-workflow)
16. [Bad Ideas & Course Corrections](#16-bad-ideas--course-corrections)
17. [Free Tools & Resources](#17-free-tools--resources)
18. [Appendix: Build Instructions](#18-appendix-build-instructions)
19. [Localization & Accessibility Plan](#19-localization--accessibility-plan)
20. [Marketing & Social Media Strategy](#20-marketing--social-media-strategy)
21. [Project Management & Team Operations](#21-project-management--team-operations)
22. [Cultural Research & Inspiration](#22-cultural-research--inspiration)

---

## 1. Project Vision

Nsolo is a digital version of a traditional Zambian mancala-style board game. It is a project under the NextPhases organisation, which will eventually house:

- A portfolio website at `nextphases.dev`
- A collection of cultural/Zambian/African games and apps
- Traditional games from various cultures
- Digital solutions built by the team

Nsolo will serve as both a **standalone downloadable game (PC)** and a **playable web game** on the portfolio site. Long-term goals include multiplayer, mobile, and a campaign/story mode.

---

## 2. Current State

### What Exists (v1.1.0)
- ✅ Core game logic (4×8 board, stone sowing, captures)
- ✅ Player vs Player mode
- ✅ AI Easy mode (random moves)
- ✅ AI Hard mode (evaluation-based)
- ✅ Procedurally generated music (menu, PvP, AI easy, AI hard)
- ✅ Dynamic music intensity (calm → intense based on score gap)
- ✅ Sound effects (wooden drop, pickup slide, capture chime, invalid, win/lose, UI click)
- ✅ Animated stone movement
- ✅ Mute button
- ✅ Score tracking & move counter
- ✅ App icon (`.ico` and `.png`)
- ✅ JAR packaging working
- ✅ EXE packaging via Launch4j (requires Java on user's machine)
- ✅ GitHub repo initialised (private, under NextPhasesDev)

### What's Missing / Needs Improvement
- ❌ No volume slider (only mute toggle)
- ❌ Mute button uses text ("🔊"/"🔇") instead of a proper icon
- ❌ No settings screen
- ❌ UI is functional but not polished (no hover effects, plain fonts)
- ❌ No in-game tutorial / how-to-play screen
- ❌ No web version
- ❌ No mobile version
- ❌ No multiplayer
- ❌ No undo/replay functionality
- ❌ No game history / stats tracking

---

## 3. Architecture Overview

### Current Structure
```
src/
├── MainMenu.java         — Entry point, main menu UI
├── NsoloGame.java        — Core game controller, board logic, UI
├── NsoloAI.java          — AI opponent (easy/hard)
├── SoundManager.java     — All audio (procedural synth)
├── StoneCell.java        — Custom JPanel for each board cell
└── resources/
    ├── icon.ico          — App icon (all sizes)
    └── icon.png          — Fallback icon
```

### Technology
- **Language:** Java (Swing for UI)
- **Audio:** `javax.sound.sampled` (procedural generation — fine for now, real audio files later)
- **Build:** Manual `javac` + `jar` commands
- **Packaging:** Launch4j for `.exe` (migrate to `jpackage` later)
- **VCS:** Git + GitHub

---

## 4. Roadmap & Phases

### Phase 1 — Polish (Current → v1.2.0 – v1.3.0) 🔧
> Make what exists feel good.

- Add mute icon (draw with `Graphics2D` — no image file needed)
- Add volume slider (separate music/SFX)
- Add in-game rules screen ("How to Play" on main menu)
- Guided first-time tutorial mode (overlay prompts while playing; see Priority list)
- Add hover effects on board cells
- Improve stone visuals (gradients, shadows)
- Consistent colour palette and fonts
- Add version display on main menu
- Keyboard shortcuts (M = mute, N = new game, Esc = quit)
- End-game summary screen (stats, rematch, return to menu)
- Code cleanup (remove `.class` from `src/`, compile to `build/`)
 - Long-term: chess.com-style analysis UX (see Priority 2 / future)

### Phase 2 — Web Version (v2.0.0) 🌐
> Get it playable in a browser.

- Rewrite game logic in **JavaScript + HTML5 Canvas** (best performance, full control)
- Deploy to `nsolo.nextphases.dev`
- Add "Play Online" and "Download for PC" options
- Portfolio landing page at `nextphases.dev`

### Phase 3 — Multiplayer (v2.1.0) 👥
> Play against friends online.

- WebSocket server on **Render.com** (free tier)
- Room system (create/join with code)
- Guest play (no accounts needed initially)
- AI bots as opponents when few players online
- Skill-based matchmaking and ranked mode (when player base grows)

### Phase 4 — Mobile (v3.0.0) 📱
> Android first, iOS later.

- Build with **Flutter** (free, one codebase for Android + iOS)
- Touch-optimised UI
- Google Play Store ($25 one-time)
- iOS only when there's demand and budget ($99/year)

### Phase 5 — Campaign & Expansion (v4.0.0) 🎮
> Story mode, more games, cultural content.

- Campaign: journey through 10 Zambian provinces with increasing AI difficulty
- Unlock board skins, stone designs, music tracks
- Add more traditional games (Bao, Oware, Morabaraba)
- Cultural info cards / educational content

### Phase 6 — NextPhases Platform (v5.0.0) 🏗️
> Full portfolio site with multiple projects.

- `nextphases.dev` as main hub
- Project showcase, team page, blog
- Traditional games section
- Contracted work / SaaS portfolio

---

## 5. TODO — Unified Prioritised Task List

> **Workflow:** Pick one or more items from the current priority level → implement → increment version → push when comfortable. Complete the current priority's milestone before moving to the next.

### ✅ Completed
- [x] Improved sound system with richer, more musical procedural audio
- [x] Different music tracks for each game mode (Menu, PvP, AI Easy, AI Hard)
- [x] Dynamic music intensity (switches to intense when game is close)
- [x] Win/Lose sound effects
- [x] Better sound effects (wooden drop, capture chime, pickup slide, UI click)
- [x] App icon (`.ico` with all sizes, `.png` fallback)
- [x] JAR packaging working
- [x] EXE packaging via Launch4j
- [x] GitHub repo initialised (private, under NextPhasesDev)

---

### 🔴 Priority 1 — Immediate Polish → v1.2.0
> **Milestone:** A polished, pleasant single-player game ready to show off, with improved accessibility and first-pass localization support.

| # | Task | Detail |
|---|------|--------|
| 1 | **Mute icon image** | ✅ Implemented as a drawn icon with `Graphics2D` (no image file). |
| 2 | **Volume slider** | Separate sliders for music and SFX volume. Save preference to a local config file (`nsolo.properties`) |
| 3 | **In-game rules screen** | "How to Play" button on main menu → shows Nsolo rules in a scrollable panel |
| 4 | **Hover effects on board cells** | Subtle glow/lighten on valid cells when hovered |
| 5 | **Improve stone visuals** | Add gradients and soft shadows to stones in `StoneCell.java` |
| 6 | **Settings panel** | Volume controls + board colour theme selector (3–4 presets) |
| 7 | **Version display on main menu** | ✅ Implemented (`VERSION` constant in `MainMenu.java`, shown on title screen). |
| 8 | **Code cleanup** | Delete `.class` files from `src/`, always compile to `build/classes` |
| 9 | **Accessibility options** | ✅ Add Settings dialog to main menu with text size (normal/large) and high contrast toggle that affect menu + in‑game UI. |
| 10 | **Language selector** | ✅ Add language selector (English / Bemba / Nyanja) in Settings and use ZedTranslate to translate the "How to Play" dialog at runtime. |
| 11 | **Guided tutorial (optional, first-time only)** | Add **popup step-by-step guidance** during a tutorial match (e.g. “click a pit in your territory”, “try this capture”, “now you’re safe”). Show it only for the first tutorial/first time a new region variant is introduced (stored as a flag in `nsolo.properties`), and allow “Skip tutorial” at any step. |

---

### 🟡 Priority 2 — Game Features → v1.3.0
> **Milestone:** A feature-complete desktop game with all quality-of-life touches.

| # | Task | Detail |
|---|------|--------|
| 1 | **Undo button** | Let players undo their last move (store previous board state) |
| 2 | **AI Medium difficulty** | Bridge Easy (random) and Hard — use simple heuristic (prefer captures, avoid giving captures) |
| 3 | **End-game summary screen** | Show final score, move count, game duration. Buttons: Rematch / Return to Menu |
| 4 | **Game stats** | Track wins/losses per mode locally (save to `nsolo-stats.json`) |
| 5 | **Keyboard shortcuts** | M = mute, N = new game, Esc = back to menu |
| 6 | **Responsive resizing** | Game window scales with screen size using `GridBagLayout` or proportional sizing |
| 7 | **Board colour themes** | 4 presets: Classic Wood, Dark Stone, Light Sand, Malachite Green |
| 8 | **Localization framework** | Replace ad‑hoc translation calls with a proper resource bundle–based system. Use ZedTranslate during development to pre‑generate strings for Bemba, Nyanja (and others) but ship static `strings_<lang>.properties` files so the game works fully offline and no API key is needed at runtime. See [Section 19](#19-localization--accessibility-plan). |
| 9 | **Analysis / review mode (future)** | Chess.com-style: show a move timeline (last move + history), per-move evaluation/difficulty hint from the AI, and a “why this is good/bad” explanation + capture opportunities. Keep it optional (Analysis mode after match) so casual games stay fast. |

---

### 🟢 Priority 3 — Web Version → v2.0.0
> **Milestone:** Nsolo playable in a browser at `nsolo.nextphases.dev`.

| # | Task | Detail |
|---|------|--------|
| 1 | **Rewrite game logic in JavaScript** | Port `NsoloGame.java` and `NsoloAI.java` to JS. Use HTML5 Canvas for rendering |
| 2 | **Web Audio API sounds** | Port procedural audio from `SoundManager.java` to Web Audio API |
| 3 | **Buy `nextphases.dev`** | Purchase from **Cloudflare Registrar** (~$12/year, cheapest `.dev` registrar) |
| 4 | **Build portfolio landing page** | Simple HTML/CSS/JS at `nextphases.dev`. Include: game description, screenshots, cultural context, download link, play online button |
| 5 | **Deploy Nsolo** | Host on **GitHub Pages** (free). Set up subdomain `nsolo.nextphases.dev` via DNS CNAME record |
| 6 | **Download page** | Link to GitHub Releases for JAR/EXE |
| 7 | **Post on itch.io** | Upload for free — get exposure from the indie community |

---

### 🔵 Priority 4 — Multiplayer → v2.1.0
> **Milestone:** Play against friends online.

| # | Task | Detail |
|---|------|--------|
| 1 | **WebSocket server** | Node.js server hosted on **Render.com** (free tier) |
| 2 | **Room system** | Create room → get 4-digit code → friend joins with code |
| 3 | **Server-authoritative state** | Server validates all moves, broadcasts board state to both clients |
| 4 | **Guest play** | No accounts needed — just pick a display name |
| 5 | **AI bot backfill** | If no human opponent available, offer NsoloBot (Easy/Hard) labelled clearly |
| 6 | **Reconnection handling** | 30-second window to rejoin if disconnected |
| 7 | **Ranked mode** | ELO rating, leaderboard, match history — only after player base exists |

---

### 🟣 Priority 5 — Mobile → v3.0.0
> **Milestone:** Nsolo on Android.

| # | Task | Detail |
|---|------|--------|
| 1 | **Set up Flutter project** | One codebase for Android + iOS |
| 2 | **Port game logic to Dart** | Translate board/AI logic from Java |
| 3 | **Touch controls** | Tap cells to play, long-press for info |
| 4 | **Google Play Store listing** | Free, $25 one-time dev fee. Android first |
| 5 | **iOS** | Only when Android is stable and there's demand ($99/year Apple fee) |

---

### ⚪ Priority 6 — Campaign & Platform → v4.0.0+
> **Milestone:** Story mode and multi-game platform.

| # | Task | Detail |
|---|------|--------|
| 1 | **Campaign mode** | 10 Zambian provinces, increasing AI difficulty, cultural facts per region |
| 2 | **Unlockables** | Board skins, stone designs, music tracks (all free, earned in-game) |
| 3 | **Additional games** | Bao (East Africa), Oware (West Africa), Morabaraba (Southern Africa) |
| 4 | **NextPhases platform** | Full `nextphases.dev` build-out: project showcase, team page, blog, games hub |
| 5 | **Real audio files** | Replace procedural audio with `.ogg`/`.wav` from Freesound.org (CC0) and OpenGameArt.org |

---

## 6. Sound & Music Plan

### Current System (v1.1.0)
All audio is procedurally generated using `javax.sound.sampled`:
- ✅ No external audio files needed — small JAR size
- ✅ Different track per mode (Menu, PvP, AI Easy, AI Hard)
- ✅ Dynamic intensity (calm → tense → intense based on score gap)
- ✅ All sound effects: wooden drop, pickup slide, capture chime, invalid buzz, win fanfare, lose fade, UI click
- ❌ Sounds are synthetic — no real instruments
- **Status:** Fine for now. Real audio files are a Priority 6 task.

### Sound Effects (Current — Procedurally Generated)
| Sound | Description |
|-------|-------------|
| Stone drop | Layered sine + noise burst, short decay, warm frequency |
| Pickup | Quick ascending two-note, snappy |
| Capture | Rich chord with harmonics, slight reverb simulation |
| Invalid | Gentle two-tone descending, not harsh |
| Win | Full chord progression, 3–4 notes with harmony |
| Lose | Soft minor chord fade, not punishing |
| Button click | Crisp pop with very short decay |

### Music — Mode-Specific Tracks (Current)
| Mode | Mood | Tempo | Key Elements |
|------|------|-------|-------------|
| Menu | Warm, inviting | 80 BPM | Pentatonic melody, gentle bass, calm |
| PvP | Energetic, fun | 110 BPM | Rhythmic, balanced, competitive feel |
| AI Easy | Relaxed, casual | 85 BPM | Light melody, soft bass, no tension |
| AI Hard | Tense, strategic | 100 BPM | Minor key, deeper bass, suspenseful |

### Dynamic Intensity System (Already Implemented)
- **CALM** — Score gap > 5 in player's favour → lighten music
- **NORMAL** — Default state
- **TENSE** — Score gap < 3 → add urgency
- **INTENSE** — Final moves / very close game → full intensity

### Mute Icon — How to Implement (Priority 1, Task #1)

**Recommended: Draw it in code with `Graphics2D`** (no image file needed).

Add a method to your mute button that paints a speaker icon:
```java
// In your mute button's paintComponent or as a custom Icon:
// Draw speaker body (small rectangle + triangle)
// If unmuted: draw 2–3 sound wave arcs
// If muted: draw a diagonal line through it
```
This avoids needing image files and scales to any size.

**Alternative: Use an image file:**
- Download from [Lucide Icons](https://lucide.dev) (MIT licence, free)
- Search for "volume-2" (unmuted) and "volume-x" (muted)
- Download as SVG → convert to 24×24 PNG at [svgtopng.com](https://svgtopng.com)
- Save as `src/resources/speaker_on.png` and `src/resources/speaker_off.png`

### Future: Real Audio Files (Priority 6)
When the project grows, replace procedural audio with real files:
- [Freesound.org](https://freesound.org) — CC0 sound effects (free, no attribution needed)
- [OpenGameArt.org](https://opengameart.org) — Free game audio (check licence per asset)
- [Incompetech.com](https://incompetech.com) — Free music by Kevin MacLeod (CC BY — must credit)

---

## 7. UI/UX Improvement Plan

### Colour Palette (Use Consistently)
| Role | Colour | Hex |
|------|--------|-----|
| Primary (board/frame) | Saddle Brown | `#8B4513` |
| Player A territory | Light Pink | `#FFB6C1` |
| Player B territory | Sky Blue | `#87CEEB` |
| Accent (highlights) | Gold | `#FFD700` |
| Text | Cornsilk | `#FFF8DC` |
| Background | Dark Brown | `#3E2723` |

### Phase 1 Improvements (v1.2.0)
1. **Hover effects** — Valid cells glow/lighten when mouse enters
2. **Selected cell highlight** — Clear visual feedback when picking up stones
3. **Score animation** — Score numbers briefly scale up on capture
4. **Better fonts** — Use `SansSerif` bold for headers, consistent sizing throughout
5. **Board border** — Wooden frame effect (draw a thick rounded rect around the board)
6. **Stone rendering** — Slightly randomise stone positions in cells for a natural look
7. **Turn indicator** — Pulse/animate the current player's territory border

### Phase 2+ Improvements (v1.3.0+)
- Board themes (wood, stone, modern, malachite)
- Particle effects on capture
- Custom cursor (hand icon on valid cells)
- Dark mode toggle

---

## 8. Web Deployment Plan

### Recommended Stack (All Free)

| Component | Tool | Cost |
|-----------|------|------|
| Domain | `nextphases.dev` via Cloudflare Registrar | ~$12/year |
| Hosting | GitHub Pages | Free |
| Game rendering | HTML5 Canvas + vanilla JS | Free |
| Backend (multiplayer, later) | Node.js on Render.com | Free tier |
| Database (later) | Supabase | Free tier |
| SSL | Included with GitHub Pages | Free |

### Deployment Steps (When Ready)
1. Buy `nextphases.dev` from Cloudflare Registrar
2. Create repo: `NextPhasesDev/nextphases.dev`
3. Build landing page (HTML/CSS/JS — no framework needed initially)
4. Add Nsolo game page
5. Enable GitHub Pages on the repo
6. Add DNS CNAME record: `nsolo` → `NextPhasesDev.github.io`
7. Game is playable at `nsolo.nextphases.dev`

### Web Game File Structure
```
nextphases.dev/
├── index.html              — Portfolio landing page
├── nsolo/
│   ├── index.html          — Nsolo game page
│   ├── game.js             — Game logic (ported from Java)
│   ├── ai.js               — AI logic
│   ├── sound.js            — Web Audio API sounds
│   └── assets/
│       ├── icon.png
│       └── (future audio files)
├── about/                  — About NextPhases
└── projects/               — Other projects
```

---

## 9. Mobile Plan

### Recommended: Flutter
- One codebase for Android + iOS
- Dart is easy to learn coming from Java
- Great for 2D board games
- Free, large community
- Google Play: $25 one-time | Apple App Store: $99/year (delay until demand exists)

### Mobile Priority
- **Android first** — free to publish, overwhelmingly dominant in Zambia/Africa
- iOS only after Android is stable and there's actual demand

---

## 10. Multiplayer Plan

### Architecture
```
Client (Browser/Desktop) ←→ WebSocket Server (Render.com) ←→ Client (Browser/Desktop)
```

### Implementation Steps
1. **Room system** — Player creates room, gets 4-digit code, friend joins with code
2. **Server-authoritative** — Server validates moves, broadcasts board state
3. **Turn management** — Server enforces turns, handles 60-second timeouts
4. **Reconnection** — 30-second window to rejoin if disconnected

### Bot Backfill & Matchmaking
- If few players online, offer **AI bots** (reuse existing AI logic, label them clearly)
- **Skill-based matchmaking** when player base grows — match by ELO rating
- **Ranked mode** unlocks after 5 casual games (prevents smurfing)
- **Casual (unranked)** mode always available

### Free Server Hosting
- **Render.com** — recommended, free tier, auto-sleep
- **Fly.io** — alternative, free tier, good latency

---

## 11. Campaign & Story Mode Plan

### Concept
Single-player journey across Zambia. Each province has unique AI difficulty, board aesthetics, music, and cultural facts.

Also add a **candy-crush style progression**:
- You start in the home region (brand name `Nsolo` / local name if different).
- As you “travel” province-to-province, you **unlock new region variants** (board skin + rules variant + local name/branding).
- When you play the **first level of a new region variant**, the game shows a short **guided lesson** (popup overlays like “click a pit here”, “try a capture from here”, “watch out for …”).

### Structure
```
Campaign Map (10 Provinces)
├── Lusaka (Tutorial — learn the rules)
├── Copperbelt (Easy AI)
├── Southern (Easy-Medium AI)
├── Eastern (Medium AI)
├── Northern (Medium-Hard AI)
├── Luapula (Hard AI)
├── Western (Hard AI, special rules variant)
├── Northwestern (Expert AI)
├── Muchinga (Expert AI, timed moves)
└── Central (Final boss — Master AI)
```

### Region Naming + Game Variants
Some mancala family games share the same core mechanic but have different local names. We will support:
- **Local brand name switching** in UI/loading screens (e.g. `Nsolo` in Zambia; `Chisolo` in certain regional contexts/variants).
- **Rule/visual variants per region** (small, targeted changes so learning stays manageable).
- The progression system ensures players learn each new variant via the **first-time guided lesson**.

### Campaign Access Rule (Important Fix)
To avoid frustrating players from specific regions, campaign progression must never block regional free play.

**Final policy:**
1. **Campaign Mode** keeps progression and story order (province-by-province unlocks).
2. **Region Quick Play** is always available from day one for all regions/variants.
3. First-time play of any region shows a short optional tutorial overlay (skippable).
4. Campaign rewards (badges/skins) stay tied to campaign completion, not quick play.

This keeps story progression meaningful while letting players immediately play their home-region variant.

### Canonical Naming + Alias Policy
Use a canonical ID per variant family, with alias names shown in UI and search.

| Canonical ID | Primary Display Name | Common Aliases to Support |
|--------------|----------------------|---------------------------|
| `nsolo` | Nsolo | Chisolo, Isolo, Kisolo |
| `oware` | Oware | Ayo, Awale, Awari, Warri, Ouril |
| `bao` | Bao | Bau |
| `omweso` | Omweso | Igisoro, Coro |
| `kalah` | Kalah | Mancala (commercial label) |
| `congkak` | Congkak | Sungka, Chongka, Dakon |
| `pallanguzhi` | Pallanguzhi | Pallankuli |
| `toguz` | Toguz Korgool | Togyzkumalak |

**Rule:** analytics, save files, and matchmaking always use canonical IDs; UI can show local aliases.

### Unlockables (All Free, Earned In-Game)
- Board skins: wood, stone, copper, malachite
- Stone designs: seeds, pebbles, gems, traditional beads
- Music tracks per region
- Cultural fact cards about each province

---

## 12. Portfolio & Domain Strategy

### Domain: `nextphases.dev`

### Subdomain Strategy (Free — Use This)
| Subdomain | Purpose |
|-----------|---------|
| `nsolo.nextphases.dev` | Direct link to Nsolo game |
| `games.nextphases.dev` | Hub for all traditional games (when multiple exist) |

**Progression:**
1. **Now:** `nsolo.nextphases.dev` — free, shareable, professional
2. **Later:** `games.nextphases.dev` — hub listing all games
3. **Only if big:** `playnsolo.com` — buy only when there's real traction

> ⚠️ Don't buy a separate game domain until the game has users. Subdomains are free and professional.

### Footer Branding (All Pages)
> **Nsolo** — A traditional Zambian board game · Built by [NextPhases](https://nextphases.dev)

### Site Structure
```
nextphases.dev
├── /                — Landing page (who we are, what we do)
├── /projects        — All projects
│   ├── /nsolo       — Nsolo (playable + download)
│   └── /...         — Future projects
├── /games           — Traditional games collection
├── /about           — Team bios
├── /blog            — Dev diary, updates (optional)
└── /contact         — Contact form
```

### Branding
- **Org name:** NextPhases
- **Tagline:** "Digital Solutions. Cultural Roots."
- **Colour scheme:** Earth tones + modern accents
- **Logo:** Design free on [Canva](https://canva.com)

### SEO
- Each game gets its own page with description, screenshots, download links
- Open Graph tags for social media sharing
- `robots.txt` and `sitemap.xml`

---

## 13. Distribution & Packaging

### Desktop (Current)
| Format | Tool | Notes |
|--------|------|-------|
| `.jar` | `javac` + `jar` CLI | Works on any OS with Java |
| `.exe` | Launch4j | Windows, requires Java installed. Set JRE path to `C:\Program Files\Common Files\Oracle\Java\javapath` |
| `.exe` installer | `jpackage` | Bundles Java (~50MB), no Java needed on user's machine. **Migrate to this.** |

### Web (Future)
- Hosted at `nsolo.nextphases.dev` — no download needed

### Mobile (Future)
- `.apk` via Google Play Store
- `.ipa` via Apple App Store (when budget allows)

### GitHub Releases (Do for Every Version)
1. `git tag v1.2.0`
2. `git push origin v1.2.0`
3. GitHub → Releases → Draft new release → select tag
4. Write release notes (copy from CHANGELOG)
5. Upload `Nsolo-1.2.0.jar` and `Nsolo-1.2.0.exe`
6. Publish

---

## 14. Versioning Policy

### Format: `MAJOR.MINOR.PATCH`

| Change Type | Bump | Example |
|-------------|------|---------|
| Bug fix, small tweak | PATCH | 1.1.0 → 1.1.1 |
| New feature, improvement | MINOR | 1.1.0 → 1.2.0 |
| Breaking change, major rewrite | MAJOR | 1.2.0 → 2.0.0 |

### Planned Version Map
| Version | Milestone |
|---------|-----------|
| v1.1.0 | ✅ Current — Sound overhaul, dynamic music |
| v1.2.0 | UI polish, volume slider, mute icon, rules screen |
| v1.3.0 | Undo, stats, AI medium, themes, localization |
| v2.0.0 | Web version launch |
| v2.1.0 | Online multiplayer |
| v3.0.0 | Mobile launch (Android) |
| v4.0.0 | Campaign mode |
| v5.0.0 | NextPhases platform + multiple games |

### Where Version Appears
1. `MainMenu.java` → `VERSION` constant
2. `CHANGELOG.md` → release notes
3. Launch4j config XML → file/product version
4. GitHub Release tag
5. In-game menu (bottom of title screen)

---

## 15. Git & Release Workflow

### Branch Strategy
```
main              ← Stable releases only
└── dev           ← Active development
    ├── feature/volume-slider
    ├── feature/rules-screen
    └── fix/capture-bug
```

### Daily Workflow
```bash
git checkout dev
# make changes...
git add .
git commit -m "feat: add volume slider to settings panel"
git push

# When ready for release:
git checkout main
git merge dev
git tag v1.2.0
git push --tags
git push
```

### Commit Message Format
```
type: short description

Types:
  feat:     new feature
  fix:      bug fix
  ui:       visual/UI change
  sound:    audio changes
  docs:     documentation
  refactor: code cleanup (no behaviour change)
  build:    build/packaging changes
```

---

## 16. Bad Ideas & Course Corrections

### ⚠️ Things to Reconsider

**1. Scope creep**
> The biggest risk for hobby projects. Finish Priority 1 (polish) completely before thinking about web/mobile. A polished v1.3.0 is worth more than a broken v2.0.0.

**2. Buying a domain before having content**
> Don't buy `nextphases.dev` until you have a landing page and Nsolo's web version ready. An empty site looks unprofessional. Set a deadline: buy the domain only when v2.0.0 (web) is ready.

**3. Multiplayer too early**
> Multiplayer is 10× harder than single-player. It needs a server, networking, state sync, error handling, reconnection, and ongoing hosting. The AI modes are your multiplayer for now. Wait until the web version is solid.

**3b. Locking regional play behind campaign only**
> Bad UX for players who want their home variant immediately. Keep campaign progression for rewards/story, but always allow Region Quick Play.

**4. iOS too early**
> Apple charges $99/year. Don't pay until Android has users and there's actual demand. Zambia/Africa is overwhelmingly Android.

**5. `.class` files in `src/`**
> Compiled files should never be in the source folder. Always compile to `build/classes`:
> ```bash
> del src\*.class
> javac -d build/classes src/*.java
> ```

**6. Launch4j with Java 25**
> Launch4j struggles with newer Java. Long-term, switch to `jpackage` which bundles Java — users don't need it installed. This is the professional method. Launch4j is fine for now but plan to migrate.

**7. No build tool**
> Manual `javac` works for small projects but gets painful as it grows. Adopt **Gradle** at v1.3.0+ — it handles compilation, JARs, dependencies, and versioning automatically.

**8. Overcomplicating sound**
> Procedural audio is fine for the current stage. Don't spend weeks on sound when UI polish and features will make a bigger impact. Real audio files (`.ogg`/`.wav`) are a Priority 6 task — do them when the project justifies it.

---

## 17. Free Tools & Resources

### Development
| Tool | Purpose | Cost |
|------|---------|------|
| IntelliJ IDEA Community | Java IDE | Free |
| VS Code | Web development | Free |
| Git + GitHub | Version control, hosting, CI/CD, releases | Free |
| Gradle | Build automation (adopt at v1.3.0+) | Free |

### Design & Assets
| Tool | Purpose | Cost |
|------|---------|------|
| Canva | Logo, graphics, social media | Free tier |
| Figma | UI mockups | Free tier |
| Lucide Icons | UI icons (MIT licence) | Free |
| Freesound.org | Sound effects (CC0) | Free |
| OpenGameArt.org | Game audio/art | Free |
| Incompetech.com | Music tracks (CC BY) | Free |
| Google Fonts | Typography | Free |

### Hosting & Deployment
| Tool | Purpose | Cost |
|------|---------|------|
| GitHub Pages | Static site hosting | Free |
| Vercel | Alternative hosting | Free tier |
| Render.com | Backend server (multiplayer) | Free tier |
| Supabase | Database + auth | Free tier |
| Cloudflare | DNS, CDN, domain registrar | Free tier (DNS/CDN) |

### Testing & Quality
| Tool | Purpose | Cost |
|------|---------|------|
| JUnit 5 | Java unit testing | Free |
| GitHub Actions | CI/CD automation | Free (2000 min/month) |

---

## 18. Appendix: Build Instructions

### Compile & Run from Source
```bash
cd C:\Users\HPG8\IdeaProjects\nsolo-v2\src
javac *.java
java MainMenu
```

### Create JAR
```bash
cd C:\Users\HPG8\IdeaProjects\nsolo-v2\src

# Compile
javac *.java

# Create manifest (if not exists)
echo Manifest-Version: 1.0> MANIFEST.MF
echo Main-Class: MainMenu>> MANIFEST.MF
echo.>> MANIFEST.MF

# Build JAR (includes resources folder)
jar cfm Nsolo.jar MANIFEST.MF *.class resources

# Test
java -jar Nsolo.jar
```

### Create EXE — Launch4j (Current Method)
1. Open Launch4j
2. Load config: `nsolo-launch4j-config.xml`
3. Verify **JRE path** is set to: `C:\Program Files\Common Files\Oracle\Java\javapath`
4. Click Build
5. Test the generated `.exe`

> **Finding your Java path:** Run `where java` in cmd. It returns the path Launch4j needs.

### Create EXE — jpackage (Recommended for Distribution)
```bash
cd C:\Users\HPG8\IdeaProjects\nsolo-v2\src

jpackage --input . ^
  --name Nsolo ^
  --main-jar Nsolo.jar ^
  --main-class MainMenu ^
  --type exe ^
  --icon resources\icon.ico ^
  --app-version 1.2.0 ^
  --win-shortcut ^
  --win-menu
```

### Clean Compiled Files from src/
```bash
del C:\Users\HPG8\IdeaProjects\nsolo-v2\src\*.class
```

---

## 19. Localization & Accessibility Plan

### Language Support
- **Phase 1 (v1.2.x):** English as base language, with live runtime translation of the "How to Play" dialog into Bemba and Nyanja via ZedTranslate (optional, depends on local API key).
- **Phase 2 (v1.3.x):** Introduce full localization framework with `ResourceBundle` and static `strings_<lang>.properties` files for English, Nyanja, Bemba.
- **Future:** Lozi, Tonga, Chewa (based on demand), ideally also pre-generated into static resources.

### Global Mancala Variant Language Coverage (Roadmapped)
This project will maintain a **variant registry** based on known mancala families and aliases (Africa, Middle East, South Asia, Southeast Asia, Central Asia, and selected global adaptations). We do not block releases on full coverage; we ship in tiers.

| Tier | Scope | Delivery Rule |
|------|-------|---------------|
| Tier 1 | Zambia-first (`Nsolo/Chisolo`) + English | Must be complete before each release |
| Tier 2 | High-visibility African variants (`Oware`, `Bao`, `Omweso`) | Add aliases + translated naming cards |
| Tier 3 | Asia/Middle East variants (`Congkak`, `Sungka`, `Pallanguzhi`, `Mangala`, `Toguz`) | Add when campaign reaches those collections |
| Tier 4 | Long-tail variants from registry list | Community-driven updates in batches |

### Where to Get Translations (Free-First)
| Use Case | Source |
|----------|--------|
| Fast draft translation | ZedTranslate / Google Translate / DeepL (where supported) |
| African language validation | Native-speaker review via local communities, campus groups, WhatsApp/Discord language groups |
| Terminology consistency | Internal glossary (`translation-glossary.md`) maintained per canonical variant ID |
| Hard-to-support languages | Human-first translation contributors + phased partial support |
| Name/alias verification | Wikipedia variant lists + BoardGameGeek + academic/game-history references |

### Regional Language Bundle Plan (Initial)
| Region Group | Languages to Prioritize |
|-------------|--------------------------|
| Zambia core | English, Bemba, Nyanja, Tonga, Lozi |
| Southern/Central Africa expansion | Swahili, French, Portuguese |
| North/East Africa expansion | Arabic, Swahili, English |
| Global accessibility | English baseline for all variants |

### Acceptance Criteria for Localization + Variants
1. Every playable variant has a canonical ID and at least one alias mapping.
2. Home-region quick play is available even if campaign progression is locked.
3. At least one reviewed language pack exists for each launched region group.
4. Missing translations gracefully fall back to English with no crashes.
5. Tutorial overlays are skippable and repeatable from settings/help.

### Language/Region Translation Sources (Recommended)
| Language | Primary Region Focus | Recommended Source for Initial Draft | Community Validation Source | Notes |
|----------|-----------------------|--------------------------------------|-----------------------------|-------|
| English (`en`) | Global baseline | Internal writing (source of truth) | Team review | Keep all game text authored in English first. |
| Bemba (`bem`) | Northern/Copperbelt/Luapula | ZedTranslate or Google Translate | Native speakers (Discord/WhatsApp campus groups) | Validate game terms manually (pit, capture, sow). |
| Nyanja (`ny`) | Lusaka/Eastern | ZedTranslate or DeepL alternative where available | Native speakers in local communities | Keep short UI labels concise for smaller buttons. |
| Tonga (`toi`) | Southern Province | Google Translate + glossary seed | Local teacher/community reviewer | Build a term glossary before full UI translation. |
| Lozi (`loz`) | Western Province | Human-first draft (limited MT quality) | Barotse cultural/community contacts | Prioritize key UI strings first due limited tooling quality. |
| Swahili (`sw`) | East/Central Africa audience | DeepL/Google Translate | East African player feedback | Useful for broader regional discoverability. |
| French (`fr`) | DRC/Central-West Africa audience | DeepL | Native speaker review | Good for expansion outside Zambia. |

### Translation Workflow (Definitive)
1. Write/update English strings in `strings_en.properties` only.
2. Export string list and machine-translate drafts (ZedTranslate/Google/DeepL).
3. Run one native-speaker review pass per language (focus on gameplay terms).
4. Save approved files as `strings_<lang>.properties` and commit to repo.
5. Ship only static files in production builds (no runtime API dependency).
6. Keep a `translation-glossary.md` for standardized terms per language.

### Implementation
- **Now (v1.2.x):**
  - Add a language selector in the main menu Settings dialog (English, Bemba, Nyanja).
  - For long text like the "How to Play" dialog, call **ZedTranslate** at runtime using an environment variable `ZEDTRANSLATE_API_KEY` (never commit the key).
  - Cache responses in memory so repeated openings of the dialog do not re‑hit the API.
  - If the free plan returns `attribution.required = true`, show the provided attribution text (e.g. "Powered by ZedTranslate") in the UI next to the translated content.
  - Always fall back to English if the API is unavailable or the key is missing, so the game remains playable offline.
- **Next (v1.3.x):**
  - Use ZedTranslate **offline during development** (with a local `.env` or environment variable, never pushed) to batch‑translate all UI strings.
  - Copy the translated text into `strings_<lang>.properties` files (one per language).
  - Switch the app to load text only from `ResourceBundle` files at runtime (no network calls, no API key in the shipped game).
  - Keep the dev‑only translation scripts/usage documented here, but ensure production builds do not depend on any external API.

### Text-to-Speech (TTS)
- **Web version:** Use browser `SpeechSynthesis` API (free, built-in)
- **Mobile:** Use platform TTS (Android `TextToSpeech` class, iOS `AVSpeechSynthesizer`)
- **Desktop Java:** Not worth the effort — skip for now

### Accessibility
- High contrast mode option
- Keyboard navigation for all menus
- Text resizing (scale UI based on system DPI)

---

## 20. Marketing & Social Media Strategy

### Account Structure
| Account | Platforms | Purpose |
|---------|-----------|---------|
| `@NextPhasesDev` | LinkedIn, X/Twitter, Instagram | Organisation brand — portfolio, contracted work, SaaS |
| `@PlayNsolo` | X/Twitter, TikTok, Instagram | Game-specific — gameplay clips, updates, cultural content |

**Why separate?** Clients don't want to see mancala memes, and gamers don't care about enterprise solutions. Keep audiences clean.

### Content Ideas (All Free)
- Dev diary posts ("Today we added dynamic music that changes when the game gets close")
- Short gameplay clips (record with OBS, edit with CapCut — both free)
- Cultural posts (Nsolo's history, mancala traditions, regional rule variations)
- Behind-the-scenes (team working, debugging stories)
- "Did you know?" facts about mancala games and Zambian provinces
- Before/after screenshots of UI improvements

### Posting Schedule
- 2–3× per week. **Consistency > volume.**
- Use **Buffer** (free tier, 3 channels) to schedule posts in advance.

### Free Tools
| Tool | Purpose |
|------|---------|
| Canva | Graphics, thumbnails, social posts |
| OBS Studio | Screen recording, gameplay capture |
| CapCut | Video editing (TikTok, Reels) |
| Buffer | Schedule posts (free: 3 channels) |
| Linktree | Single bio link (all your links) |

### Launch Strategy (When Web Version is Ready)
1. **Pre-launch (3–4 weeks before):** Post dev progress, cultural facts, teasers, behind-the-scenes
2. **Launch day:** Post everywhere — X, Instagram, TikTok, Reddit. Ask friends/family to share. Subreddits: r/indiegaming, r/boardgames, r/africa, r/zambia, r/gamedev
3. **Post-launch (1 week after):** "Thank you + what's next" post, share player feedback, show numbers
4. **Ongoing:** Updates when features drop, highlight user feedback, run challenges ("Beat Hard AI — screenshot your score")

### Communities to Target
- **Reddit:** r/indiegaming, r/boardgames, r/africa, r/zambia, r/gamedev, r/indiedev
- **itch.io** — Post free, get indie exposure
- **Discord servers** — Game dev communities, indie showcase channels
- **University groups** — Gaming clubs, CS/engineering societies
- **Zambian tech communities** — BongoHive, local tech meetups, WhatsApp groups

> ⚠️ **Don't pay for ads** until you have organic traction. Paid marketing with zero audience is wasteful.

---

## 21. Project Management & Team Operations

### Recommended Setup (All Free)
| Tool | Purpose |
|------|---------|
| **Notion** | Task management, docs, wikis (you already have this — improve it) |
| **GitHub Projects** | Kanban board tied directly to code issues and PRs |
| **Discord** | Team communication (text, voice, video) |
| **Google Drive** | Shared files, assets, designs |

### How to Organise Multiple Projects
Use **GitHub Projects** (free kanban boards) — one board per project:
- **Board: Nsolo** — columns: Backlog → In Progress → Review → Done
- **Board: NextPhases Website** — same columns
- **Board: BICCDP Website** — same columns
- **Board: Other SaaS/Contract Work** — same columns

Each board pulls from GitHub Issues in the relevant repo. This keeps everything tied to the code.

### Team Workflow
- **Weekly standup** (15 min, voice on Discord): What did you do? What's next? Any blockers?
- **GitHub Issues** for all tasks — assign to people, set milestones
- **PR reviews** before merging to `main` (even if the team is small — builds good habits)

### Team Roles (Flexible — People Wear Multiple Hats)
- Project lead / lead dev (you)
- Frontend / UI
- Backend / multiplayer
- Sound / art
- Marketing / community

---

## 22. Cultural Research & Inspiration

### Goals
- Authentic representation of Zambian culture
- Educate players about the game's cultural significance
- Differentiate from generic mancala apps

### Sources
- [Wikipedia: Mancala](https://en.wikipedia.org/wiki/Mancala) — game family overview
- [Wikipedia: Nsolo](https://en.wikipedia.org/wiki/Nsolo) — specific game rules and history
- Local Zambian elders, cultural centres, museums
- Books: *Mancala Games* by Larry Russ (covers African variants)

### In-Game Implementation
- Cultural fact cards on loading screens and in campaign mode
- Traditional Zambian colour palettes and patterns in board themes
- Region-specific music styles in campaign provinces
- Credits page acknowledging cultural origins

### Touring & Research Trips (Long-Term)
How game studios do cultural research:
1. **Partner with local cultural organisations** — museums, heritage centres, universities
2. **Attend cultural festivals** — Kuomboka, Nc'wala, Likumbi Lya Mize
3. **Interview elders and traditional game players** — document rules, strategies, stories
4. **Photography and audio recording** — capture textures, sounds, environments for game assets
5. **Document everything** — blog posts, social media content, dev diary

> This is a long-term goal. For now, online research and personal knowledge is enough. When there's budget or an opportunity, a research trip would produce incredible content for both the game and marketing.

---

*This document is the single source of truth for the Nsolo project. Update it as decisions are made and milestones are hit. Mark completed items with ✅ and add dates.*

*— NextPhases, February 2026*

