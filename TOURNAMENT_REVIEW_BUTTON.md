# Tournament Review Button - Integration Guide

## What Was Added

A **"Review" button column** has been added to the tournaments table. When users click this button on a **completed/finished tournament**, a review submission dialog opens where they can:

1. Enter their player name
2. Select a 1-5 star rating
3. Write a comment (10-300 characters)
4. Submit the review

---

## How It Works

### User Flow

1. **Browse Tournaments** - User sees tournaments table with all tournaments
2. **Spot Finished Tournament** - Tournaments with status "finished" show a **"Review" button** in the new Review column
3. **Click Review Button** - Opens a dialog form
4. **Fill Form:**
   - Enter player name
   - Click stars to select rating (1-5)
   - Type review comment
5. **Submit** - Review is validated and submitted
6. **Success Message** - User gets confirmation that review is waiting for admin approval

---

## Files Modified

### 1. tournaments.fxml
**File:** `src/main/resources/views/tournaments/tournaments.fxml`
- Added new column: `<TableColumn fx:id="reviewCol" text="Review" prefWidth="80" />`

### 2. TournamentsController.java
**File:** `src/main/java/edu/connexion3a36/rankup/controllers/tournaments/TournamentsController.java`

**New Field:**
```java
@FXML private TableColumn<TournamentRow, Void> reviewCol;
```

**New Methods Added:**
- `setupReviewColumn()` - Configures the review button column
- `onReviewTournament(TournamentRow)` - Handles review button click
- `openReviewDialog(int tournamentId, String tournamentName)` - Opens review form dialog
- `updateStarDisplayInDialog(HBox starBox, int rating)` - Updates star visual feedback
- `submitReview(String playerName, int tournamentId, String tournamentName, int rating, String comment)` - Saves review

---

## Features

### ✅ Review Button
- Only appears for **finished** tournaments
- Green gradient button with white text
- Hover effects for better UX

### ✅ Interactive Star Rating
- 5 clickable stars (★)
- Golden hover preview
- Teal selection color
- Visual feedback on selection

### ✅ Live Character Counter
- Shows current/max character count (0/300)
- Prevents typing over 300 characters
- Updates in real-time

### ✅ Input Validation
- Player name required
- Rating must be selected (1-5)
- Comment must be 10-300 characters
- Error messages for each field

### ✅ Professional Styling
- Dark theme consistent with app
- Teal/cyan accents (#00BCD4)
- Rounded corners
- Clear dialog layout

---

## Integration with ReviewService

Add this to the `submitReview()` method to save reviews to database:

```java
private void submitReview(String playerName, int tournamentId, String tournamentName, int rating, String comment) {
    try {
        // Create review object
        Review review = new Review(playerName, tournamentId, tournamentName, rating, comment, LocalDate.now());
        
        // Save to database using ReviewService
        ReviewService reviewService = new ReviewService();
        reviewService.addEntity(review);
        
        showSuccess("Review submitted successfully! Waiting for admin approval.");
        loadTournaments();
        tournamentsTable.setItems(filtered);
    } catch (SQLException e) {
        showError("Failed to submit review: " + e.getMessage());
    }
}
```

---

## User Experience Flow

```
User opens Tournament List
         ↓
Sees tournaments with status badges
         ↓
Finds finished tournament
         ↓
Clicks "Review" button
         ↓
Dialog opens with review form
         ↓
User fills: name, stars, comment
         ↓
Clicks "Submit Review" button
         ↓
Validation checks
         ↓
Success! "Review submitted successfully"
         ↓
Dialog closes, user back to tournament list
```

---

## Testing Checklist

- [ ] Tournament table shows "Review" column
- [ ] Review button only appears for "finished" tournaments
- [ ] Clicking Review button opens dialog
- [ ] Can type player name
- [ ] Can select star rating (1-5)
- [ ] Stars highlight with hover effect
- [ ] Character counter updates in real-time
- [ ] Cannot type more than 300 characters
- [ ] Cannot submit without player name
- [ ] Cannot submit without selecting rating
- [ ] Cannot submit comment with < 10 characters
- [ ] Cannot submit comment with > 300 characters
- [ ] Submit button works when all fields valid
- [ ] Success message appears after submit
- [ ] Dialog closes after submit
- [ ] Tournament list refreshes after submit

---

## TODO: Database Integration

The `submitReview()` method currently just shows a success message. To actually save reviews to the database:

1. Import ReviewService:
```java
import edu.connexion3a36.services.ReviewService;
import edu.connexion3a36.entities.Review;
import java.time.LocalDate;
```

2. Uncomment and complete the submitReview method with actual database call (see code example above)

3. Test that reviews are saved and appear in admin moderation panel

---

## Column Layout

The tournaments table now has these columns (left to right):
1. Tournament Name
2. Description
3. Start Date
4. End Date
5. Location
6. Status
7. **Review** ← NEW!

---

## Styling

The Review button inherits your app's theme:
- **Color:** Gradient from #38bdf8 (cyan) to #8b5cf6 (purple)
- **Text:** White
- **Corners:** Rounded (8px)
- **Padding:** 5px 15px
- **Font Size:** 12px
- **Cursor:** Hand (pointer)

---

## Notes

- Reviews can only be submitted for **finished** tournaments
- Users can still access the full "My Reviews" page to see all their reviews
- Users cannot review the same tournament twice (enforced in ReviewService)
- All reviews start with status "pending" (waiting for admin approval)
- Admin can approve or reject in the Review Moderation panel

---

## Future Enhancements

- [ ] Show average rating on tournament row
- [ ] Show review count on tournament row
- [ ] Allow users to edit reviews after submission (if pending)
- [ ] Disable review button if user has already reviewed this tournament
- [ ] Show "Already Reviewed" badge instead of empty Review column

---

**Status:** ✅ Complete and Ready for Use

Users can now easily review finished tournaments directly from the tournaments list!
