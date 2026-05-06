# RAWG API Integration - Quick Start

## What Has Been Added

Your project now includes a complete game search system powered by RAWG API with:

### 🎮 Components Created
1. **Game Entity** - Represents game data structure
2. **RAWGGameService** - Handles all API communications
3. **GameSearchController** - Manages UI interactions
4. **game-search.fxml** - Beautiful search interface
5. **Gson Dependency** - JSON parsing library

### 📋 Dependencies Updated
- Added: `com.google.code.gson:gson:2.10.1` to pom.xml

---

## Quick Integration Steps

### Step 1: Get API Key (2 minutes)
1. Go to https://rawg.io/api
2. Sign up for free account
3. Get your API key
4. Update `RAWGGameService.java`:
   ```java
   private static final String API_KEY = "your_api_key_here";
   ```

### Step 2: Add to Navigation Menu
Edit your main navigation view and add:

```xml
<Button text="Game Search" onAction="#openGameSearch" />
```

Then in your navigation controller:

```java
@FXML
public void openGameSearch() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/views/games/game-search.fxml")
        );
        VBox gameView = loader.load();
        // Add to your main content area
        mainContent.getChildren().setAll(gameView);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

### Step 3: Compile & Run
```bash
mvn clean install
mvn javafx:run
```

---

## Features Overview

### 🔍 Search Games
- Type game name and press Enter or click "Search"
- Results display in real-time

### ⭐ View Game Info
Each game card shows:
- Title
- Rating (out of 5)
- Release Date
- Genres
- Supported Platforms
- Review Count
- Description

### 🔗 Quick Actions
- **View Details** - See full information
- **Open on RAWG** - Opens game page in browser

### 📈 Discover Popular Games
- Click "Popular Games" button
- Shows top 10 highest-rated games

---

## File Locations

```
src/main/java/
├── edu/connexion3a36/
│   ├── entities/
│   │   └── Game.java                    ✨ NEW
│   ├── services/
│   │   └── RAWGGameService.java         ✨ NEW
│   └── rankup/controllers/
│       └── GameSearchController.java    ✨ NEW

src/main/resources/
└── views/games/
    └── game-search.fxml                 ✨ NEW

pom.xml                                   ✅ UPDATED (Gson added)
```

---

## Usage Examples

### In Your Application

**Load the search view:**
```java
FXMLLoader loader = new FXMLLoader(
    getClass().getResource("/views/games/game-search.fxml")
);
VBox gameSearchView = loader.load();
```

**Programmatically search for games:**
```java
RAWGGameService service = new RAWGGameService();
List<Game> results = service.searchGames("Minecraft");

for (Game game : results) {
    System.out.println(game.getName() + " - Rating: " + game.getRating());
}
```

**Get specific game details:**
```java
Game game = service.getGameDetails(3328); // Elden Ring
System.out.println("Release: " + game.getReleaseDate());
System.out.println("Genres: " + Arrays.toString(game.getGenres()));
```

---

## Configuration Options

### API Key Setup (Choose One)

**Option A: Direct Replacement (Quick)**
```java
// In RAWGGameService.java
private static final String API_KEY = "YOUR_ACTUAL_KEY";
```

**Option B: Environment Variable (Recommended)**
```bash
# Set environment variable
set RAWG_API_KEY=your_api_key
```

Then modify:
```java
private static final String API_KEY = System.getenv("RAWG_API_KEY");
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "No API Key" error | Add your API key to RAWGGameService.java |
| Search returns nothing | Verify game name spelling; check internet connection |
| "API ERROR: 429" | Rate limit exceeded - wait 1 minute |
| Browser won't open links | Ensure Desktop.isDesktopSupported() returns true |
| UI freezes during search | Check if thread is properly handling exceptions |

---

## What's Next?

### 💡 Enhancement Ideas
1. **Add Search Filters** - Filter by genre, platform, rating
2. **Caching** - Store recent searches locally
3. **Favorites** - Save favorite games to database
4. **Screenshots** - Display game artwork
5. **Game Comparison** - Compare multiple games

### 🔧 Advanced Customization
- Modify card styling in `createGameCard()` method
- Add more game properties from RAWG API
- Implement pagination for large result sets
- Add sort options (rating, date, reviews)

---

## API Rate Limits

RAWG Free Tier:
- 20 requests/minute
- ~10,000 requests/month

**Best Practice**: Implement result caching for repetitive searches

---

## Need Help?

- RAWG API Docs: https://api.rawg.io/docs/
- Check RAWG_API_SETUP.md in project root for detailed guide
- Review GameSearchController.java for customization examples

---

## Summary

✅ Complete RAWG API integration added  
✅ Search, popul games, and game details ready  
✅ Beautiful game card UI with ratings  
✅ Direct links to RAWG for more info  
✅ Background threading prevents UI freeze  
✅ Full error handling  

**Ready to use - just add your API key!**
