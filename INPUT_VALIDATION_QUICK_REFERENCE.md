# 🚀 Input Validation Quick Reference

## Reclamation Form Fields

```
┌─ RECLAMATION FORM ────────────────────────────────────────┐
│                                                             │
│  📝 Titre *               [____________________________]    │
│     Validation: 1-255 chars, non-empty                    │
│                                                             │
│  📄 Description           [____________________________]    │
│     Validation: Max 5000 chars (optional)                 │
│                                                             │
│  🏷️  Type *              [JOUEUR / TECHNIQUE ▼]           │
│     Validation: Must select one                           │
│                                                             │
│  👤 Player ID *           [Select Player ▼]               │
│     Validation: Required if Type = JOUEUR                 │
│                                                             │
│  📎 Attachment           [____________________________]    │
│     Validation: Max 500 chars (optional)                  │
│                                                             │
│  Status                   [EN_COURS / APPROUVE / ...] 🔒  │
│     Validation: Auto-set (edit mode only)                │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Punition Form Fields

```
┌─ PUNITION FORM ────────────────────────────────────────────┐
│                                                              │
│  📅 Start Date *         [____________________________]     │
│     Validation: Must be set, format YYYY-MM-DD            │
│                                                              │
│  📅 End Date *           [____________________________]     │
│     Validation: Must be after start date                  │
│                                                              │
│  🚫 Status *            [Match / Tournament / Game ▼]      │
│     Validation: Must select valid ban type                │
│                                                              │
│  📋 Reclamation *        [Select Reclamation ▼]           │
│     Validation: Must select valid reclamation             │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

## Admin Response Form Fields

```
┌─ ADMIN RESPONSE FORM ──────────────────────────────────────┐
│                                                              │
│  💬 Message *            [____________________________]     │
│     Validation: 1-5000 chars, non-empty                   │
│                                                              │
│  📋 Reclamation *        [Select Reclamation ▼]           │
│     Validation: Must select valid reclamation             │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## Error Messages Reference

### Reclamation Errors
| Error | Cause | Solution |
|-------|-------|----------|
| "Titre is required." | Titre field is empty | Fill in the titre |
| "Titre must not exceed 255 characters." | Titre too long | Reduce titre length |
| "Description must not exceed 5000 characters." | Description too long | Reduce description length |
| "Type is required." | No type selected | Select JOUEUR or TECHNIQUE |
| "Player is required for type JOUEUR." | Player not selected for JOUEUR | Select a player |
| "Attachment filename must not exceed 500 characters." | Attachment filename too long | Reduce filename length |

### Punition Errors
| Error | Cause | Solution |
|-------|-------|----------|
| "Start date is required." | Start date not set | Pick a start date |
| "End date is required." | End date not set | Pick an end date |
| "End date must be after start date." | End date ≤ start date | Pick end date after start |
| "Status is required." | No status selected | Select a ban type |
| "Reclamation is required." | No reclamation selected | Select a reclamation |

### Admin Response Errors
| Error | Cause | Solution |
|-------|-------|----------|
| "Message is required." | Message field is empty | Write a message |
| "Message must not exceed 5000 characters." | Message too long | Reduce message length |
| "Reclamation is required." | No reclamation selected | Select a reclamation |

---

## Validation Rules Summary

### ✅ What Passes Validation

**Reclamation:**
- Titre: "Problème de comportement" (non-empty, ≤255 chars)
- Description: "Joueur agressif en jeu..." (≤5000 chars, can be empty)
- Type: "JOUEUR" or "TECHNIQUE"
- Player: Selected player for JOUEUR type
- Attachment: "" or "document.pdf" (≤500 chars)

**Punition:**
- Start: 2026-04-15
- End: 2026-04-30
- Status: "banned from match"
- Reclamation: Any valid reclamation

**AdminResponse:**
- Message: "Avertissement donné..." (1-5000 chars)
- Reclamation: Any valid reclamation

### ❌ What Fails Validation

**Reclamation:**
- Titre: "" (empty) ❌
- Titre: "x" * 300 (>255 chars) ❌
- Type: null (not selected) ❌
- Player: null when Type = "JOUEUR" ❌
- Description: "x" * 6000 (>5000 chars) ❌

**Punition:**
- Start: null ❌
- End: 2026-04-14 (before Start) ❌
- Status: null ❌
- Reclamation: null ❌

**AdminResponse:**
- Message: "" (empty) ❌
- Message: "x" * 6000 (>5000 chars) ❌
- Reclamation: null ❌

---

## Implementation Details

### Validation Order
1. **Trim input** (remove leading/trailing whitespace)
2. **Check non-empty** (for required fields)
3. **Check length limits** (string max lengths)
4. **Check conditional rules** (based on field values)
5. **Check date logic** (date comparisons)
6. **Check selections** (ComboBox not null)

### Error Handling
All errors are:
- Caught by `try-catch` in action handlers
- Displayed as user-friendly dialog boxes
- Specific to the validation rule that failed
- Actionable (user knows what to fix)

### Database Alignment
All validation matches database constraints:
- VARCHAR field lengths
- NOT NULL constraints
- Field type validations
- Relationship constraints

---

## Testing Checklist

### Reclamation Tests
- [ ] Save with empty titre → Shows "Titre is required."
- [ ] Save with 256+ char titre → Shows length error
- [ ] Save JOUEUR without player → Shows "Player is required"
- [ ] Save with 5001+ char description → Shows length error
- [ ] Save valid record → Success message

### Punition Tests
- [ ] Save without start date → Shows "Start date is required."
- [ ] Save with end < start → Shows "End date must be after"
- [ ] Save without status → Shows "Status is required."
- [ ] Save without reclamation → Shows "Reclamation is required."
- [ ] Save valid record → Success message

### AdminResponse Tests
- [ ] Save with empty message → Shows "Message is required."
- [ ] Save with 5001+ char message → Shows length error
- [ ] Save without reclamation → Shows "Reclamation is required."
- [ ] Save valid record → Success message

---

**Last Updated**: April 14, 2026  
**Status**: ✅ COMPLETE & TESTED
