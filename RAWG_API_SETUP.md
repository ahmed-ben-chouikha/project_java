# RAWG API Integration Guide

## Overview
This project now includes a game search feature powered by the RAWG Video Games Database API. Users can search for games, view details, ratings, and links to the official RAWG pages.

## Setup Instructions

### Step 1: Get Your RAWG API Key

1. Visit [https://rawg.io/api](https://rawg.io/api)
2. Click on "Get API Key" or "Sign Up"
3. Create a free account
4. Copy your API key

### Step 2: Configure the API Key

**Option 1: Environment Variable (Recommended for Production)**

Add to your system environment variables:
```
RAWG_API_KEY=your_api_key_here
```

Then modify `RAWGGameService.java` to read from environment:
```java
private static final String API_KEY = System.getenv("RAWG_API_KEY") != null ? 
                                       System.getenv("RAWG_API_KEY") : "YOUR_API_KEY_HERE";
```

**Option 2: Direct Configuration (Quick Setup)**

In `src/main/java/edu/connexion3a36/services/RAWGGameService.java`, replace:
```java
private static final String API_KEY = "YOUR_API_KEY_HERE";
```

With your actual API key:
```java
private static final String API_KEY = "your_actual_api_key_here";
```

### Step 3: Build the Project

Run Maven to install the new Gson dependency:
```bash
mvn clean install
```

## Project Components

### New Dependencies Added
- **Gson 2.10.1**: For JSON parsing from RAWG API responses

### New Entity Class
- **`Game.java`** (`edu.connexion3a36.entities.Game`)
  - Represents a video game with properties: id, name, rating, genres, platforms, release date, etc.

### New Service Class
- **`RAWGGameService.java`** (`edu.connexion3a36.services.RAWGGameService`)
  - Provides methods to:
    - `searchGames(String query)` - Search for games by name
    - `getGameDetails(long gameId)` - Get detailed information for a specific game
    - `getPopularGames(int limit)` - Get top-rated games

### New Controller
- **`GameSearchController.java`** - Handles UI interactions for game search

### New Views
- **`game-search.fxml`** - FXML layout for the game search interface

## Usage

### Adding Game Search to Your Navigation

In your main navigation controller or menu, add a link to load the game search view:

```java
// Load the game search view
FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/games/game-search.fxml"));
VBox gameSearchView = loader.load();
// Add to your main layout or scene
```

### Basic Usage Examples

**Search for Games:**
```java
RAWGGameService gameService = new RAWGGameService();
List<Game> results = gameService.searchGames("Elden Ring");
```

**Get Popular Games:**
```java
List<Game> popularGames = gameService.getPopularGames(20);
```

**Get Game Details:**
```java
Game gameDetails = gameService.getGameDetails(3328);
```

## Features

✅ **Search Functionality**
- Search games by name
- Results display in real-time
- Background threading prevents UI freezing

✅ **Game Information Display**
- Game title and rating
- Release date
- Genres and platforms
- Description (truncated)
- Review count

✅ **Direct Links**
- "Open on RAWG" button opens the game's RAWG page in default browser
- "View Details" button shows full information in dialog

✅ **Popular Games**
- One-click load of top-rated games
- Perfect for discovering new games

## API Response Handling

The service automatically handles:
- JSON parsing using Gson
- Error codes and HTTP exceptions
- Missing/null fields in responses
- Data extraction for nested objects (genres, platforms)

## Error Handling

- Network errors are caught and displayed to user
- Missing API key shows helpful error message
- Invalid searches return empty results with "No results found" message

## Data Privacy & Rate Limiting

### RAWG API Limits (Free Tier)
- **Rate Limit**: 20 requests per minute for free tier
- **Request Limit**: 10,000 requests per month (approximately)

Best practices:
- Implement request caching for frequently searched games
- Add delays between rapid successive requests
- Store recent search results

### Data Storage
- Game data is **NOT stored locally** in the database
- Each search/request fetches fresh data from RAWG
- No personal user data is transmitted to RAWG

## Customization

### Modify API Call Parameters

Search with additional filters:
```java
String url = RAWG_API_BASE_URL + "/games?search=" + encodedQuery 
           + "&ordering=-rating" // Order by rating
           + "&page_size=50"      // Results per page
           + "&key=" + API_KEY;
```

### Add More Game Properties

Update `Game.java` entity to include additional RAWG API fields:
- esrb_rating
- metacritic_score
- developers
- publishers
- etc.

Then parse these in `RAWGGameService.parseGameObject()` method.

## Troubleshooting

### "No results found" when searching
- Verify API key is correct
- Check game name spelling
- Try a different game name
- Verify internet connection

### "API ERROR: 401"
- Invalid or missing API key
- Follow Step 2 to configure API key properly

### "API ERROR: 429"
- Rate limit exceeded
- Wait a minute before making more requests
- Implement caching for repeated searches

### UI Freezes During Search
- Search is already running in background thread
- This shouldn't happen - if it does, close and reopen the search view

## RAWG API Documentation

Full RAWG API documentation: [https://api.rawg.io/docs/](https://api.rawg.io/docs/)

Key endpoints used:
- `/games` - List/search games
- `/games/{id}` - Get game details
- `/genres` - List game genres
- `/platforms` - List platforms

## Future Enhancements

Potential features to add:
1. **Advanced Filters**: Filter by genre, platform, release year
2. **Search History**: Cache recent searches
3. **Favorites**: Save favorite games locally
4. **Screenshots**: Display game screenshots from RAWG
5. **Reviews**: Show user reviews from RAWG
6. **Game Comparison**: Compare multiple games side-by-side
7. **Database Integration**: Store game favorites in MySQL

## Support

For issues with RAWG API:
- Visit: [https://rawg.io/](https://rawg.io/)
- Contact RAWG support directly

For application issues:
- Check the console output for error messages
- Verify all configuration steps above
- Ensure Maven dependencies are properly installed
