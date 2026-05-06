# Notifications System - Visual Guide

## 📍 Location on Screen

```
┌─────────────────────────────────────────────────┐
│  Reclamations Page                   🔔 [2]    │  ← Bell icon with badge
├─────────────────────────────────────────────────┤
│  [Search field] [Clear Form]                    │
├─────────────────────────────────────────────────┤
│  Form fields for creating reclamations...       │
├─────────────────────────────────────────────────┤
│  List of reclamations                           │
│                                                 │
└─────────────────────────────────────────────────┘

[2] = Red badge showing 2 unread notifications
```

## 🔔 Bell Icon States

### State 1: No Notifications
```
🔔  (No badge visible)
```

### State 2: With Notifications
```
🔔
 [1]  ← Red badge with count
```

## 📬 Notification Popup (Automatic)

When new admin response arrives:

```
┌──────────────────────────────────────────┐
│  New Admin Response                      │
├──────────────────────────────────────────┤
│                                          │
│  Response to: Technical Support Issue    │
│                                          │
│  "We have reviewed your issue and found │
│   that the bug was in the authentication│
│   system. This has been fixed in the    │
│   latest version."                       │
│                                          │
│              [OK]                        │
└──────────────────────────────────────────┘
```

## 📋 Notifications List Dialog (Manual)

When clicking 🔔 bell icon:

```
┌─────────────────────────────────────────────────┐
│  Notifications                                  │
│  Recent Admin Responses (3)                     │
├─────────────────────────────────────────────────┤
│  📋 Technical Support Issue                     │
│  ⏰ 2026-05-05 14:30:22                         │
│  💬 We have reviewed your issue and found...   │
│  ────────────────────────────────────────────  │
│                                                │
│  📋 Account Verification Problem               │
│  ⏰ 2026-05-05 13:15:45                         │
│  💬 Your account has been verified. You can... │
│  ────────────────────────────────────────────  │
│                                                │
│  📋 Tournament Registration Error              │
│  ⏰ 2026-05-05 12:00:00                         │
│  💬 The tournament slot has been reserved...   │
│  ────────────────────────────────────────────  │
│                                                │
│                            [OK]                │
└─────────────────────────────────────────────────┘
```

## 🔄 Data Flow Diagram

```
                    ADMIN ADDS RESPONSE
                           │
                           ▼
                     Database Updated
                     (admin_response table)
                           │
                           ▼
               Background Thread (Every 15s)
                  checkForNewAdminResponses()
                           │
                ┌──────────┴──────────┐
                │                     │
            NEW RESPONSE?          SKIP
                │
                ▼
        Create Notification Object
        ReclamationNotification
                │
                ▼
        Add to seenAdminResponseIds
        (prevent duplicates)
                │
                ▼
        Add to notifications list
        Platform.runLater()
                │
        ┌───────┴───────┐
        │               │
        ▼               ▼
    Show Popup    Update Badge
    Alert           Count
```

## 🧵 Thread Lifecycle

```
Page Load
   │
   ▼
initialize()
   │
   ▼
startNotificationPolling()
   │
   ▼
notificationPollingThread = new Thread()
   │
   ├─ setDaemon(true)  ← Non-blocking
   │
   ▼
thread.start()
   │
   ▼
┌─────────────────────────────┐
│ while(!interrupted)         │
│  ├─ checkForNewResponses()  │
│  ├─ Thread.sleep(15000)     │
│  └─ repeat                  │
└─────────────────────────────┘
   │
   ▼
App Closes → Thread terminates
(daemon won't block shutdown)
```

## 📊 Notification Tracking

```
First Run:
  seenAdminResponseIds = {}  (empty set)
  notifications = []  (empty list)

User creates reclamation, admin responds:
  seenAdminResponseIds = {1}  ← Response ID 1 added
  notifications = [Notif(id=1, message="...")]  ← Added to front
  Badge shows: 1

Admin creates another response:
  seenAdminResponseIds = {1, 2}
  notifications = [Notif(id=2), Notif(id=1)]  ← Newest first
  Badge shows: 2

User clicks bell, views list:
  Shows both notifications
  seenAdminResponseIds still = {1, 2}
  (tracking, not marking as "read" in DB)
```

## ⏱️ Timing Example

```
14:00:00 - Admin creates response
14:00:05 - User still working (no notification yet)
14:00:10 - User still working (no notification yet)
14:00:15 - Background thread checks database
14:00:15 - NEW RESPONSE DETECTED!
14:00:15 - Popup appears immediately
14:00:15 - Badge updates to "1"
14:00:30 - Next poll (nothing new)
14:15:00 - Next poll (nothing new)
```

## 🎨 Badge Styling

```
BEFORE (No notifications):
┌──┐
│🔔│
└──┘

AFTER (With notifications):
┌──┐
│🔔│ ← Red background
│[2]│  ← Number in center
└──┘  
```

## 💾 Database Query Flow

```
Background Thread
   │
   ▼
adminResponseService.getData()
   │
   ├─ SQL: SELECT * FROM admin_response
   │       ORDER BY created_at DESC
   │
   ▼
Returns List<AdminResponse>
   │
   ├─ For each response
   │  └─ Check if ID in seenAdminResponseIds
   │
   ├─ If NEW:
   │  ├─ adminResponseService.getReclamationById()
   │  │  └─ SQL: SELECT * FROM reclamation WHERE id=?
   │  │
   │  ├─ Create ReclamationNotification
   │  │
   │  └─ Update UI on JavaFX thread
   │
   ▼
Sleep 15 seconds, repeat
```

## 🔐 Thread Safety

```
Background Thread              JavaFX UI Thread
     │                               │
     ├─ Check database               │
     │                               │
     ├─ Find new response            │
     │                               │
     ├─ Create object                │
     │                               │
     └─ Platform.runLater() ────────→ │
                                     │
                      ┌──────────────┘
                      │
                      ├─ notifications.add(...)
                      │
                      ├─ updateBadge()
                      │
                      └─ showPopup()
```

## 📝 Message Display Format

```
Admin Response: "This is a long admin message that explains 
everything about the issue and how we resolved it with 
detailed instructions for the user."

In List View (100 char limit):
💬 This is a long admin message that explains everything ab...
                                                          ^^^
                                                   Truncated
```

## ✅ Checklist - System Ready

- ✅ Bell icon appears in top-right
- ✅ Badge hidden when no notifications
- ✅ Badge shows count > 0
- ✅ Background thread started on page load
- ✅ Polls every 15 seconds
- ✅ Popup appears for new responses
- ✅ Click bell shows all notifications
- ✅ Notifications sorted by newest first
- ✅ Styling consistent with theme
- ✅ Thread is daemon (non-blocking)

---

**Visual Reference Complete** - System is fully operational! 🚀

