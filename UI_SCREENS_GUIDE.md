# EsportDev Arena - UI Screen Guide

## Application Flow

```
                          ┌─────────────────────────────┐
                          │      MainFx.java            │
                          │   (Application Entry)       │
                          └──────────────┬──────────────┘
                                         │
                                         ▼
                          ┌─────────────────────────────┐
                          │   Main Dashboard Shell      │
                          │  (AjouterPersonne.fxml)     │
                          └──────────────┬──────────────┘
                                         │
                    ┌────────┬───────────┼───────────┬────────┐
                    ▼        ▼           ▼           ▼        ▼
               Dashboard  Matches    Tournaments   Teams    User/Admin
               
                    └────────┬───────────┼───────────┬────────┘
                             │           │           │
                    ┌────────┴───┐   ┌───┴──────┐  │
                    ▼            ▼   ▼          ▼  │
              Tournament Hub ─────────────────────┘
            (AfficherPersonne2.fxml)
            
             [Back to Dashboard]
```

---

## Screen 1: Dashboard (Home)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                               │
│  ┌────────────────────────────┐  EsportDev Dashboard  [User ▼] [Admin ▼]   │
│  │                            │  Live matches, tournaments, teams...        │
│  │   EsportDev               │                                              │
│  │   Arena Control           │ ┌─────────────────────────────────────────┐ │
│  │                            │ │ Season 12 live now                      │ │
│  │ [Dashboard]              │ │ Compete, watch, and join the bracket   │ │
│  │ [Matches]                │ │                                        │ │
│  │ [Tournaments]            │ │ [Join Tournament Hub] [View Matches]  │ │
│  │ [Teams]                  │ └─────────────────────────────────────────┘ │
│  │ [User Area]              │                                              │
│  │ [Admin Area]             │  ┌──────────┬──────────┬──────────┬────────┐ │
│  │                            │ │    12    │    8     │    24    │    5   │ │
│  │ Built with JavaFX         │ │ LIVE     │ OPEN     │FEATURED │ ADMIN  │ │
│  │                            │ │MATCHES   │SLOTS     │TEAMS    │ACTIONS │ │
│  │                            │ └──────────┴──────────┴──────────┴────────┘ │
│  └────────────────────────────┘                                              │
│                                                                               │
│  Live Matches                                                                │
│  Follow the action across the biggest brackets                              │
│  ┌──────────────────────────┐  ┌──────────────────────────┐                │
│  │ Valorant       [LIVE]    │  │ LoL        [UP NEXT]     │                │
│  │ Nova vs Eclipse          │  │ Titan vs Pixel Pulse     │                │
│  │ Quarterfinal • 19:30     │  │ Semifinal • 20:15        │                │
│  │ [Watch now]              │  │ [View bracket]           │                │
│  └──────────────────────────┘  └──────────────────────────┘                │
│                                                                               │
│  Upcoming Tournaments                                                        │
│  Open slots and prize pools for teams ready to join                         │
│  ┌──────────────────────────┐  ┌──────────────────────────┐                │
│  │ Summer Clash    [OPEN]   │  │ Midnight Circuit [OPEN]  │                │
│  │ Valorant                 │  │ CS2                      │                │
│  │ 12 May • $15K • 8/16     │  │ 18 May • $10K • 12/16    │                │
│  │ [Join now]               │  │ [Join now]               │                │
│  └──────────────────────────┘  └──────────────────────────┘                │
│                                                                               │
│  Featured Teams                                                              │
│  Top rosters ready for the next season                                      │
│  ┌──────────────────────────┐  ┌──────────────────────────┐                │
│  │ Eclipse                  │  │ Apex Drift               │                │
│  │ Rex • Nova • Byte • ...  │  │ Milo • Vex • Raze • ...  │                │
│  │ EU West • 14W / 3L       │  │ NA Central • 11W / 5L    │                │
│  │ [View roster]            │  │ [View roster]            │                │
│  └──────────────────────────┘  └──────────────────────────┘                │
│                                                                               │
│  User Snapshot                                                               │
│  Your tournament shortcuts and highlights                                   │
│  ┌──────────────────────────┐  ┌──────────────────────────┐                │
│  │ Join streak              │  │ Personal rewards         │                │
│  │ Keep weekly bonus active │  │ Redeem drops & badges    │                │
│  └──────────────────────────┘  └──────────────────────────┘                │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Screen 2: Match Center

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                               │
│  ┌────────────────────────────┐  Match Center              [User ▼] [Admin] │
│  │                            │  Track live battles, upcoming showdowns   │
│  │   EsportDev               │                                              │
│  │   Arena Control           │  ┌──────────────────────────────────────────┐ │
│  │                            │  │                                          │ │
│  │ [Dashboard]              │  │ ┌────────────────────────────────────┐   │ │
│  │ [Matches]                │  │ │ Valorant            [LIVE] 🔴      │   │ │
│  │ [Tournaments]            │  │ │ Nova Crew vs Eclipse               │   │ │
│  │ [Teams]                  │  │ │ Quarterfinal • 19:30 GMT           │   │ │
│  │ [User Area]              │  │ │ [Watch now]                        │   │ │
│  │ [Admin Area]             │  │ └────────────────────────────────────┘   │ │
│  │                            │  │ ┌────────────────────────────────────┐   │ │
│  │ Built with JavaFX         │  │ │ League of Legends   [UP NEXT]      │   │ │
│  │                            │  │ │ Titan Forge vs Pixel Pulse         │   │ │
│  │                            │  │ │ Semifinal • 20:15 GMT              │   │ │
│  │                            │  │ │ [View bracket]                     │   │ │
│  │                            │  │ └────────────────────────────────────┘   │ │
│  └────────────────────────────┘  │ ┌────────────────────────────────────┐   │ │
│                                  │ │ CS2                 [OPEN]         │   │ │
│                                  │ │ Shadow Unit vs Apex Drift          │   │ │
│                                  │ │ Showmatch • 22:00 GMT              │   │ │
│                                  │ │ [View bracket]                     │   │ │
│                                  │ └────────────────────────────────────┘   │ │
│                                  │                                          │ │
│                                  └──────────────────────────────────────────┘ │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Broadcast queue                                                      │   │
│  │ Pin next match, publish highlights, or switch to admin review      │   │
│  │                                          [Open tournament hub]       │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Screen 3: Tournament Hub (Join & Register)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                               │
│  [← Back to Arena] Tournament Hub        [User ▼] [Admin ▼]                 │
│                   Choose a bracket and register your roster                 │
│                                                                               │
│  Join the action                                                             │
│  Open slots, prize pools, and bracket status at a glance                    │
│                                                                               │
│  ┌──────────────────────────┐  ┌──────────────────────────┐                │
│  │ Summer Clash    [OPEN]   │  │ Midnight Circuit [OPEN]  │                │
│  │ Valorant                 │  │ CS2                      │                │
│  │ 12 May • $15K • 8/16     │  │ 18 May • $10K • 12/16    │                │
│  │ [Join]                   │  │ [Join]                   │                │
│  └──────────────────────────┘  └──────────────────────────┘                │
│                                                                               │
│  ┌──────────────────────────────────────┐  ┌─────────────────────────────┐ │
│  │ Register your squad                  │  │ Player focus                │ │
│  │ Fill in the form and secure a spot   │  │ See best tournaments,       │ │
│  │ in an open tournament.               │  │ open slots, team-friendly   │ │
│  │                                      │  │ events.                     │ │
│  │ Tournament:                          │  │ • Reserve tournament slot   │ │
│  │ [Summer Clash ▼]                     │  │ • Check match start times   │ │
│  │                                      │  │ • Track prize pools & rules │ │
│  │ Gamer tag:                           │  │                             │ │
│  │ [Your gamer tag_______]              │  │                             │ │
│  │                                      │  │                             │ │
│  │ Team name:                           │  │                             │ │
│  │ [Your team name_______]              │  │                             │ │
│  │                                      │  │                             │ │
│  │ [Join tournament]                    │  │                             │ │
│  │                                      │  │                             │ │
│  │ Registered NebulaRush from Nova      │  │                             │ │
│  │ Crew for Summer Clash.               │  │                             │ │
│  └──────────────────────────────────────┘  └─────────────────────────────┘ │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Screen 4: Teams Showcase

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                               │
│  ┌────────────────────────────┐  Featured Teams            [User ▼] [Admin] │
│  │                            │  Explore rosters, regions, competitive...   │
│  │   EsportDev               │                                              │
│  │   Arena Control           │                                              │
│  │                            │  ┌──────────────────────────┐               │
│  │ [Dashboard]              │  │ Eclipse                  │               │
│  │ [Matches]                │  │ Rex•Nova•Byte•Kaze•Lynx │               │
│  │ [Tournaments]            │  │ EU West                  │               │
│  │ [Teams]                  │  │ 14W / 3L                 │               │
│  │ [User Area]              │  │ [View roster]            │               │
│  │ [Admin Area]             │  └──────────────────────────┘               │
│  │                            │  ┌──────────────────────────┐               │
│  │ Built with JavaFX         │  │ Apex Drift               │               │
│  │                            │  │ Milo•Vex•Raze•Sol•Flux  │               │
│  │                            │  │ NA Central               │               │
│  │                            │  │ 11W / 5L                 │               │
│  │                            │  │ [View roster]            │               │
│  │                            │  └──────────────────────────┘               │
│  │                            │  ┌──────────────────────────┐               │
│  │                            │  │ Shadow Unit              │               │
│  │                            │  │ Kai•Ember•Drift•Zen•... │               │
│  │                            │  │ APAC                     │               │
│  │                            │  │ 17W / 2L                 │               │
│  │                            │  │ [View roster]            │               │
│  │                            │  └──────────────────────────┘               │
│  │                            │                                              │
│  └────────────────────────────┘                                              │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Screen 5: User Area

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                               │
│  ┌────────────────────────────┐  User Area                 [User ▼] [Admin] │
│  │                            │  Your profile, rewards, tournament shortcuts
│  │   EsportDev               │                                              │
│  │   Arena Control           │  ┌──────────────────────┐  ┌──────────────┐ │
│  │                            │  │ Player profile       │  │ Fast actions │ │
│  │ [Dashboard]              │  │                      │  │              │ │
│  │ [Matches]                │  │ Gamertag: NebulaRush │  │ • Reserve    │ │
│  │ [Tournaments]            │  │ Rank: Diamond III    │  │   tournament │ │
│  │ [Teams]                  │  │ Status: Eligible     │  │ • Check      │ │
│  │ [User Area]              │  │ for 3 tournaments    │  │   reminders  │ │
│  │ [Admin Area]             │  │                      │  │ • Open join  │ │
│  │                            │  │                      │  │   page       │ │
│  │ Built with JavaFX         │  │                      │  │              │ │
│  │                            │  │                      │  │[Open Page]   │ │
│  │                            │  └──────────────────────┘  └──────────────┘ │
│  │                            │                                              │
│  │                            │  Recommended Tournaments                    │
│  │                            │  Join one of the open events below          │
│  │                            │  ┌──────────────────────┐  ┌──────────────┐ │
│  │                            │  │ Summer Clash [OPEN]  │  │ Midnight     │ │
│  │                            │  │ Valorant • $15K      │  │ Circuit      │ │
│  │                            │  │ 8/16 • May 12        │  │ [OPEN]       │ │
│  │                            │  │ [Join now]           │  │ [Join now]   │ │
│  │                            │  └──────────────────────┘  └──────────────┘ │
│  │                            │                                              │
│  └────────────────────────────┘                                              │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Screen 6: Admin Console

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                               │
│  ┌────────────────────────────┐  Admin Console             [User] [Admin ▼]  │
│  │                            │  Approve teams, schedule, manage broadcast
│  │   EsportDev               │                                              │
│  │   Arena Control           │  ┌──────────────────────┐  ┌──────────────┐ │
│  │                            │  │ Operations           │  │ Admin tools  │ │
│  │ [Dashboard]              │  │                      │  │              │ │
│  │ [Matches]                │  │ Pending approvals:   │  │ • Approve    │ │
│  │ [Tournaments]            │  │ 4 rosters            │  │   teams      │ │
│  │ [Teams]                  │  │                      │  │ • Moderate   │ │
│  │ [User Area]              │  │ Live brackets:       │  │   chat       │ │
│  │ [Admin Area]             │  │ 2 in progress        │  │ • Schedule   │ │
│  │                            │  │                      │  │   finals     │ │
│  │ Built with JavaFX         │  │ Moderation queue:    │  │              │ │
│  │                            │  │ 6 tickets            │  │[Open tourns] │ │
│  │                            │  │                      │  │              │ │
│  │                            │  └──────────────────────┘  └──────────────┘ │
│  │                            │                                              │
│  │                            │  Moderation Queue                            │
│  │                            │  Suggested actions for the next admin shift  │
│  │                            │  ┌──────────────────────┐  ┌──────────────┐ │
│  │                            │  │ Roster review        │  │ Bracket sync │ │
│  │                            │  │ Nova Crew            │  │ Quarterfinal │ │
│  │                            │  │ Approve squad details│  │ 2 • Update   │ │
│  │                            │  │                      │  │ match result │ │
│  │                            │  └──────────────────────┘  └──────────────┘ │
│  │                            │                                              │
│  └────────────────────────────┘                                              │
│                                                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Color Legend

| Element | Color | Hex Code |
|---------|-------|----------|
| Background | Dark Blue Gradient | #07111f → #111827 |
| Text (Primary) | Light Blue | #e5eefb |
| Text (Secondary) | Medium Blue | #9fb1c7 |
| Accent (Primary) | Cyan → Purple | #38bdf8 → #8b5cf6 |
| Accent (Highlight) | Bright Cyan | #8bd8ff |
| Card Background | Dark Semi-transparent | rgba(15, 23, 42, 0.86) |
| Badge (Live) | Red | #ef4444 |
| Badge (Open) | Green | #10b981 |
| Badge (Muted) | Gray | #64748b |
| Border | Subtle Gray | rgba(148, 163, 184, 0.12) |

---

## Interaction Guide

### Navigation
- **Sidebar buttons**: Click to switch between main screens
- **"Back to Arena"** button: Return from Tournament Hub to Dashboard
- **Role toggles** (User/Admin): Switch context-specific views
- **Scroll**: All content areas are scrollable for longer lists

### Actions
- **Primary buttons** (gradient blue-purple): Join, register, approve actions
- **Secondary buttons** (gray): View, explore, more info actions
- **Match status badges**: LIVE (red), UP NEXT (blue), OPEN (green)
- **Tournament badges**: OPEN (green), FULL (gray)

### Form Interaction
- **ChoiceBox**: Click to select tournament
- **TextField**: Type gamer tag and team name
- **Submit**: Click "Join tournament" to register
- **Feedback**: Status message appears below form

---

## Responsive Behavior

- **Window**: Minimum 1200×820px (scales up to 1440×960px default)
- **Sidebar**: Fixed width 280px
- **Cards**: Auto-wrap in FlowPane with 16px gap
- **Content**: Scrolls vertically when exceeds viewport
- **Text**: Wraps on smaller screens

---

## Animation & Effects

- **Glass cards**: Subtle drop shadow (26px blur)
- **Hover states**: Button opacity changes (92%), background color shifts
- **Nav active state**: Gradient border highlight with pulse effect
- **Focus states**: Blue border highlight on form inputs
- **Transitions**: Smooth CSS transitions for color/opacity changes

---

## Accessibility

- **Contrast**: Light text on dark backgrounds (WCAG AA compliant)
- **Button sizing**: Minimum 44px for touch targets
- **Labels**: Descriptive text for all form fields
- **Keyboard**: Full keyboard navigation support (Tab, Enter, Space)
- **Screen readers**: Semantic FXML structure

---

**Ready to build?** Run: `mvn clean javafx:run`


