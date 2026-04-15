# Review & Rating System - Complete Implementation Guide

## Overview
A complete Review & Rating system for the RankUp E-Sports Platform with user submission, admin moderation, and comprehensive validation.

## ✅ What Has Been Created

### 1. Database Schema
**File:** `database/reviews_table.sql`
- Reviews table with all required fields
- Foreign key relationship to tournaments table
- Validation constraints (rating 1-5, comment length 10-300)
- Unique constraint on player-tournament combination (prevents duplicate reviews)
- Status: pending/approved/rejected
- Rejection reason tracking

### 2. Entity Class
**File:** `src/main/java/edu/connexion3a36/entities/Review.java`
- Complete Review entity with getters/setters
- Input validation in setters
- Proper constructors for creation and retrieval scenarios
- Timestamps for audit trail

### 3. Service Layer
**File:** `src/main/java/edu/connexion3a36/services/ReviewService.java`
**Interface:** `src/main/java/edu/connexion3a36/interfaces/IReview.java`

#### Methods Implemented:
- `addEntity(Review)` - Submit new review with validation
- `updateEntity(int id, Review)` - Edit pending reviews
- `deleteEntity(Review)` - Delete pending/rejected reviews
- `getData()` - Get all reviews
- `getReviewsByPlayer(String)` - Retrieve player's reviews
- `getReviewsByTournament(int)` - Get reviews for a tournament
- `getReviewsByStatus(String)` - Filter by status
- `getPendingReviews()` - Admin moderation queue
- `hasReviewedTournament(String, int)` - Check duplicate
- `approveReview(int)` - Admin approval
- `rejectReview(int, String)` - Admin rejection with reason

#### Validation Implemented:
- No empty fields
- Player name required
- Valid tournament ID
- Rating must be 1-5
- Comment: minimum 10 characters, maximum 300 characters
- Cannot review same tournament twice
- Only pending reviews can be edited
- Only pending/rejected reviews can be deleted

### 4. User Interface - Review Submission

**File:** `src/main/resources/views/tournaments/tournament-reviews.fxml`

#### Features:
- Dark-themed with teal/cyan accents (#00BCD4)
- **Form Section:**
  - Player name text field
  - Tournament dropdown (shows only confirmed tournaments)
  - Star rating selector (1-5 stars, interactive highlighting)
  - Comment textarea with live character counter (0/300)
  - Auto-filled date field (today)
  - Submit & Clear buttons
  - Real-time input validation with error messages

- **My Reviews Table:**
  - Tournament name
  - Star rating display
  - Comment
  - Review date
  - Status badge (pending/approved/rejected)
  - Rejection reason (if applicable)
  - Edit button (pending reviews only)
  - Delete button (pending reviews only)
  - Empty state when no reviews exist

### 5. User Controller
**File:** `src/main/java/edu/connexion3a36/rankup/controllers/TournamentReviewsController.java`

#### Key Features:
- Loads only confirmed tournaments for the logged-in player
- Interactive star rating selector with hover effects
- Real-time character counter with validation
- Comprehensive input validation
- Success/Error messages displayed inline
- Edit functionality for pending reviews
- Delete functionality with confirmation dialog
- Prevents duplicate reviews
- Displays rejection reasons
- Automatic form clearing after submission

### 6. Admin Interface - Review Moderation

**File:** `src/main/resources/views/admin/admin-review-moderation.fxml`

#### Features:
- **Statistics Panel:**
  - Pending review count (yellow)
  - Approved review count (green)
  - Rejected review count (red)
  - Refresh button for live updates

- **Reviews Table:**
  - Player name
  - Tournament name
  - Star rating display
  - Comment
  - Submission date
  - Current status (color-coded badges)
  - Action buttons (Approve/Reject)
  - Empty state when no pending reviews

### 7. Admin Controller
**File:** `src/main/java/edu/connexion3a36/rankup/controllers/AdminReviewModerationController.java`

#### Features:
- Displays only pending reviews by default
- Color-coded status badges
- Approve with one click
- Reject with reason input dialog
- Real-time statistics updates
- Success/Error messaging
- Automatic refresh after action
- Star rating display in table

---

## 🔧 Integration Steps

### Step 1: Database Setup
```sql
-- Run this SQL file to create the reviews table
source database/reviews_table.sql
```

### Step 2: Import Classes
The following classes are created and ready to use:
- `edu.connexion3a36.entities.Review`
- `edu.connexion3a36.interfaces.IReview`
- `edu.connexion3a36.services.ReviewService`
- `edu.connexion3a36.rankup.controllers.TournamentReviewsController`
- `edu.connexion3a36.rankup.controllers.AdminReviewModerationController`

### Step 3: Navigation Menu Setup
Add navigation items in your main menu:

**For Users:**
```java
// Add to your navigation/dashboard
MenuItem reviewsItem = new MenuItem("My Reviews");
reviewsItem.setOnAction(e -> loadScene("views/tournaments/tournament-reviews.fxml"));
```

**For Admins:**
```java
// Add to admin menu
MenuItem moderationItem = new MenuItem("Review Moderation");
moderationItem.setOnAction(e -> loadScene("views/admin/admin-review-moderation.fxml"));
```

### Step 4: Session Management - IMPORTANT ⚠️

Currently, the controllers use a placeholder: `CURRENT_PLAYER = "DefaultPlayer"`

**You need to replace this with actual session management:**

#### Option 1: Static Session Manager (Recommended)
Create a `SessionManager` class:
```java
public class SessionManager {
    private static String currentPlayer;
    private static int currentUserId;
    
    public static void setCurrentPlayer(String player) {
        currentPlayer = player;
    }
    
    public static String getCurrentPlayer() {
        return currentPlayer;
    }
}
```

Update the controller:
```java
private static final String CURRENT_PLAYER = SessionManager.getCurrentPlayer();
```

#### Option 2: Dependency Injection
Pass the player name to the controller via FXMLLoader:
```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("tournament-reviews.fxml"));
TournamentReviewsController controller = loader.getController();
controller.setCurrentPlayer(playerName);
```

---

## 📋 Features Summary

### User-Side Features ✓
- ✅ View only confirmed tournaments for review
- ✅ Submit reviews with 1-5 star rating
- ✅ Textarea with 10-300 character validation
- ✅ Visual star selector (not just numbers)
- ✅ Auto-filled review date (today)
- ✅ Live character counter
- ✅ See all their reviews with status
- ✅ Edit pending reviews
- ✅ Delete pending reviews
- ✅ View rejection reasons
- ✅ Cannot review same tournament twice
- ✅ Input validation with error messages
- ✅ Success/error alerts in UI

### Admin-Side Features ✓
- ✅ View all pending reviews in queue
- ✅ See review statistics (pending/approved/rejected)
- ✅ Approve reviews
- ✅ Reject reviews with reason
- ✅ See all review details
- ✅ Real-time updates
- ✅ Filter by status
- ✅ Color-coded status badges

---

## 🎨 Styling Applied

All components follow your dark theme:
- Background: #07111f / #0b1324
- Accent Color: #00BCD4 (teal/cyan)
- Text: white (#e5eefb)
- Buttons: Gradient from #38bdf8 to #8b5cf6
- Borders: rgba(148, 163, 184, 0.12)
- Status Colors:
  - Pending: #FFD700 (gold)
  - Approved: #34A853 (green)
  - Rejected: #ff6b6b (red)

---

## 📝 Database Operations

### View All Reviews
```sql
SELECT * FROM reviews ORDER BY created_at DESC;
```

### View Pending Reviews (Admin Queue)
```sql
SELECT * FROM reviews WHERE status = 'pending';
```

### View Reviews by Player
```sql
SELECT * FROM reviews WHERE player_name = 'PlayerName';
```

### View Reviews for a Tournament
```sql
SELECT * FROM reviews WHERE tournament_id = 1;
```

### Statistics
```sql
SELECT status, COUNT(*) as count FROM reviews GROUP BY status;
```

---

## ⚙️ Configuration Notes

### Important: Controller Properties
Both controllers reference `IService<Tournament>` methods. Ensure the `TournamentService.java` has:
- `getData()` - returns all tournaments
- `getTournamentById(int id)` - returns single tournament

### Required Existing Services
The controllers depend on:
- `TournamentService` - exists ✓
- `TournamentRegistrationService` - exists ✓
  - Must have: `getPlayerRegistrations(String playerName)`
  - Must have: `getRegistrationsByPlayer(String playerName)` OR use `getPlayerRegistrations`

---

## 🐛 Troubleshooting

### Issue: "No confirmed tournaments showing"
**Solution:** Check that tournament_registration table has entries with status = 'confirmed'

### Issue: "Column 'name' not found in tournaments table"
**Solution:** The Tournament entity uses `name` field. Verify your tournaments table schema has this column.

### Issue: Session player is always null
**Solution:** Implement the SessionManager pattern (see Integration Steps - Step 4)

### Issue: Reviews not saving
**Solution:** 
1. Verify reviews_table.sql was executed
2. Check MySQL connection is working
3. Verify comment length is 10-300 characters
4. Ensure player hasn't reviewed same tournament before

---

## 🔒 Security Considerations

- ✅ SQL injection prevention via PreparedStatements
- ✅ Input validation on all user inputs
- ✅ Duplicate review prevention via UNIQUE constraint
- ✅ Edit/Delete authorization (only pending reviews)
- ✅ Player can only see their own reviews
- ⚠️ TODO: Add role-based access control (admin vs user)
- ⚠️ TODO: Add audit logging for admin actions
- ⚠️ TODO: Add CSRF protection

---

## 🚀 Future Enhancements

1. **Admin Features:**
   - Bulk approve/reject
   - Search and filter
   - Export reviews to CSV
   - View analytics (average rating per tournament)
   - Admin notes/comments on reviews

2. **User Features:**
   - Edit comments even after approval
   - Review history/timeline
   - Sort and filter personal reviews
   - Response to rejection

3. **Rating Analytics:**
   - Average rating per tournament
   - Rating distribution charts
   - Most helpful reviews
   - Player rating badges

4. **Notifications:**
   - Email notifications on review status change
   - Rejection reason notifications
   - Review count badges

---

## 📞 Testing Checklist

- [ ] Database table created successfully
- [ ] User can view confirmed tournaments
- [ ] User can submit a review
- [ ] Character counter works correctly
- [ ] Cannot submit with empty fields
- [ ] Cannot review same tournament twice
- [ ] Edit button works for pending reviews
- [ ] Delete button works for pending reviews
- [ ] Admin can see pending reviews
- [ ] Admin can approve a review
- [ ] Admin can reject a review with reason
- [ ] Rejection reason shows on user dashboard
- [ ] Statistics update correctly
- [ ] Styling matches dark theme with teal accents

---

## 📁 File Structure Created

```
src/main/java/edu/connexion3a36/
├── entities/
│   └── Review.java
├── interfaces/
│   └── IReview.java
├── services/
│   └── ReviewService.java
└── rankup/controllers/
    ├── TournamentReviewsController.java
    └── AdminReviewModerationController.java

src/main/resources/
├── views/
│   ├── tournaments/
│   │   └── tournament-reviews.fxml
│   └── admin/
│       └── admin-review-moderation.fxml
└── styles/
    └── esports.css (already exists, matches theme)

database/
└── reviews_table.sql
```

---

## 🎯 Completion Status

✅ **Database Schema** - Complete  
✅ **Entity Classes** - Complete  
✅ **Service Layer** - Complete  
✅ **Input Validation** - Complete  
✅ **User UI** - Complete  
✅ **Admin UI** - Complete  
✅ **Controllers** - Complete  
✅ **Styling** - Complete  
⏳ **Session Integration** - Needs your implementation (see Step 4)  
⏳ **Navigation Integration** - Needs your implementation  

---

**Version:** 1.0  
**Created:** 2026-04-14  
**Status:** Ready for Integration & Testing
