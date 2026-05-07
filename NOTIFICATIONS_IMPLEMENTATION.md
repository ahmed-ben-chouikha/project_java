# Reclamation Notifications System

## Overview
Added real-time notification system to the Reclamations page that displays admin responses in real-time.

## Features Added

### 1. **Notification Icon in Top Right**
   - Bell icon (🔔) button in the top-right corner of the Reclamations page
   - Red notification badge showing count of unread notifications
   - Automatically updates when new admin responses arrive

### 2. **Real-Time Polling (Every 15 Seconds)**
   - Background daemon thread polls for new admin responses
   - Tracks seen admin responses to avoid duplicates
   - Runs continuously in the background without blocking UI

### 3. **Automatic Pop-Up Notifications**
   - When a new admin response is detected, a popup alert appears immediately
   - Shows:
     - Response title
     - Admin message content
   - Non-blocking notification that doesn't interfere with user workflow

### 4. **Notifications List Dialog**
   - Click the bell icon to view all notifications
   - Shows all recent admin responses sorted by newest first
   - Displays:
     - Reclamation title
     - Timestamp (formatted as yyyy-MM-dd HH:mm:ss)
     - Admin message (truncated to 100 chars with "...")
   - Professional formatting with separators

## Files Modified/Created

### New Files Created:
1. **`src/main/java/edu/connexion3a36/rankup/models/ReclamationNotification.java`**
   - Model class to hold notification data
   - Contains: id, adminResponseId, adminMessage, reclamationId, reclamationTitre, timestamp, read status
   - Includes formatted timestamp method

### Modified Files:

1. **`src/main/resources/views/reclamations/reclamations.fxml`**
   - Added notification button (bell icon) in top-right HBox
   - Added notification badge label
   - Both styled with btn-primary and icon-button classes

2. **`src/main/java/edu/connexion3a36/rankup/controllers/reclamations/ReclamationsController.java`**
   - Added imports for:
     - `AdminResponse`, `AdminResponseService`
     - `ReclamationNotification`, `Platform`
     - `HashSet`, `Set` for tracking seen notifications
   - Added FXML fields:
     - `@FXML private Button notificationButton;`
     - `@FXML private Label notificationBadge;`
   - Added instance variables:
     - `AdminResponseService adminResponseService`
     - `ObservableList<ReclamationNotification> notifications`
     - `Set<Integer> seenAdminResponseIds` - tracks which admin responses we've already seen
     - `Thread notificationPollingThread` - background polling thread
   - Added methods:
     - `onOpenNotifications()` - handles notification icon click
     - `startNotificationPolling()` - starts background polling thread
     - `checkForNewAdminResponses()` - polls database for new responses every 15 seconds
     - `updateNotificationBadge()` - updates badge count and visibility
     - `showNotificationPopup()` - displays immediate popup when response arrives
     - `showNotificationsDialog()` - displays list of all notifications
     - `truncateMessage()` - truncates long messages for display

3. **`src/main/resources/styles/esports.css`**
   - Added `.notification-badge` style:
     - Red background (#ef4444)
     - White text, small font (11px)
     - Bold weight (700)
     - Rounded corners with background-radius: 10
     - Padding and min-width for proper sizing

## How It Works

1. **Initialization**
   - When reclamations page loads, `startNotificationPolling()` is called
   - Creates and starts a daemon background thread

2. **Background Polling**
   - Thread sleeps for 15 seconds between checks
   - Calls `checkForNewAdminResponses()` periodically
   - Thread is daemon, so it won't prevent app from closing

3. **Detection**
   - Fetches all AdminResponses from database
   - Checks if ID is in `seenAdminResponseIds` set
   - If new, creates `ReclamationNotification` object
   - Adds to notifications ObservableList on UI thread using `Platform.runLater()`

4. **User Notification**
   - Immediate popup alert appears (non-blocking)
   - Badge updates to show unread count
   - User can click bell icon to view all notifications

5. **Notifications Dialog**
   - Shows all notifications in formatted list
   - Most recent first
   - Click-friendly display

## Technical Details

- **Thread Safety**: Uses `Platform.runLater()` to update UI from background thread
- **Memory Efficient**: Only stores notification IDs in `seenAdminResponseIds` set
- **No Database Queries on Init**: Notifications only poll when page is active
- **Non-Blocking**: Daemon thread won't prevent app shutdown
- **Clean Architecture**: Model class, controller methods, service integration

## Styling
- Notification badge appears as red circular badge with count
- Bell icon uses existing btn-primary and icon-button styles
- Professional dark theme consistent with esports styling

## Future Enhancements (Optional)
- Add database persistence for notifications (read/unread status)
- Add notification sounds
- Add notification filtering/search
- Add ability to mark notifications as read
- Add notification history retention
- Adjustable polling interval

