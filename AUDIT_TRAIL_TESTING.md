# Audit Trail Testing Guide

**Component:** AuditTrail.tsx
**API Endpoint:** `/api/activities/all`
**Last Updated:** 2025-11-24
**Status:** ✅ Implementation Complete - Ready for Testing

---

## Overview

The Audit Trail component provides a comprehensive, filterable view of all vehicle changes in the system. It supports filtering, pagination, export to CSV, and printing for compliance and accountability purposes.

---

## Features Implemented

### 1. **Data Fetching**
- ✅ Fetches from `/api/activities/all` endpoint
- ✅ Includes JWT authentication header
- ✅ Auto-loads on component mount
- ✅ Manual refresh capability

### 2. **Statistics Dashboard**
- ✅ Total Changes count (all filtered logs)
- ✅ Temporary changes count (blue card with Clock icon)
- ✅ Return changes count (green card with CheckCircle icon)
- ✅ Permanent changes count (purple card with AlertCircle icon)

### 3. **Filtering System**
- ✅ **Date Range Filter:**
  - Start date picker
  - End date picker
  - Filters logs between specified dates (inclusive)
- ✅ **Change Type Filter:**
  - All Types (default)
  - Temporary
  - Return
  - Permanent
- ✅ **Staff Name Filter:**
  - Case-insensitive text search
  - Partial match support
- ✅ **Clear Filters Button:**
  - Resets all filters to default
  - Resets pagination to page 1

### 4. **Pagination**
- ✅ 10 items per page
- ✅ Previous/Next navigation buttons
- ✅ Current page indicator
- ✅ Disabled state for first/last pages
- ✅ Auto-reset to page 1 when filters change

### 5. **Export Functionality**
- ✅ **CSV Export:**
  - Exports filtered data only
  - Headers: Timestamp, Change Type, Old Vehicle, New Vehicle, Staff Name, Reason
  - Proper CSV formatting with quoted cells
  - Auto-downloads with date-stamped filename
- ✅ **Print:**
  - Triggers browser print dialog
  - Custom print styles (via window.print())

### 6. **UI/UX Features**
- ✅ Color-coded change type badges
- ✅ Icon indicators for each change type
- ✅ Staff member avatars with gradient backgrounds
- ✅ Responsive design (mobile-friendly)
- ✅ Loading state with spinner
- ✅ Error state with retry button
- ✅ Empty state message
- ✅ Hover effects and transitions

---

## Test Plan

### **Test Suite 1: Data Loading**

#### TC1.1: Initial Load
**Steps:**
1. Navigate to Audit Trail page
2. Observe loading state
3. Wait for data to load

**Expected:**
- Loading spinner appears
- After load, statistics cards show correct counts
- Table displays first 10 logs
- No errors in console

#### TC1.2: Manual Refresh
**Steps:**
1. Click "Refresh" button
2. Observe refresh animation

**Expected:**
- Button shows spinning icon
- Data reloads from API
- Statistics update if data changed
- Button returns to normal state

#### TC1.3: Error Handling
**Steps:**
1. Stop backend server
2. Refresh Audit Trail page
3. Click "Try Again" button after error appears

**Expected:**
- Error message displays with AlertCircle icon
- "Try Again" button appears
- Clicking "Try Again" attempts to reload data

---

### **Test Suite 2: Statistics Cards**

#### TC2.1: Total Changes
**Steps:**
1. Load Audit Trail with no filters
2. Count total logs manually
3. Compare with "Total Changes" card

**Expected:**
- Count matches total logs in database (should be 12 from DataLoader)

#### TC2.2: Change Type Counts
**Steps:**
1. Apply filter for "Temporary" only
2. Check statistics cards

**Expected:**
- Total Changes = count of temporary changes
- Temporary card = same count
- Returns card = 0
- Permanent card = 0

#### TC2.3: Statistics Update on Filter
**Steps:**
1. Set start date filter to recent date
2. Observe statistics cards update

**Expected:**
- All statistics cards update to reflect filtered data
- Counts match visible filtered logs

---

### **Test Suite 3: Date Range Filtering**

#### TC3.1: Start Date Only
**Steps:**
1. Clear all filters
2. Set start date to middle of data range (e.g., 2025-11-15)
3. Observe filtered results

**Expected:**
- Only logs from 2025-11-15 onwards appear
- Earlier logs are hidden
- Statistics update correctly

#### TC3.2: End Date Only
**Steps:**
1. Clear all filters
2. Set end date to middle of data range
3. Observe filtered results

**Expected:**
- Only logs up to and including end date appear
- Later logs are hidden
- Statistics update correctly

#### TC3.3: Date Range
**Steps:**
1. Set start date: 2025-11-15
2. Set end date: 2025-11-20
3. Observe filtered results

**Expected:**
- Only logs between 2025-11-15 and 2025-11-20 appear
- Statistics show counts for that range only
- Pagination resets to page 1

#### TC3.4: Invalid Range (End before Start)
**Steps:**
1. Set start date: 2025-11-20
2. Set end date: 2025-11-15

**Expected:**
- No logs displayed (valid behavior - empty result)
- Or ideally: validation prevents this (enhancement opportunity)

---

### **Test Suite 4: Change Type Filtering**

#### TC4.1: Filter by Temporary
**Steps:**
1. Select "Temporary" from Change Type dropdown
2. Observe results

**Expected:**
- Only TEMPORARY change logs appear
- All entries have blue badge with Clock icon
- Statistics show Temporary count > 0, others = 0

#### TC4.2: Filter by Return
**Steps:**
1. Select "Return" from Change Type dropdown
2. Observe results

**Expected:**
- Only RETURN change logs appear
- All entries have green badge with CheckCircle icon
- Statistics show Returns count > 0, others = 0

#### TC4.3: Filter by Permanent
**Steps:**
1. Select "Permanent" from Change Type dropdown
2. Observe results

**Expected:**
- Only PERMANENT change logs appear
- All entries have purple badge with AlertCircle icon
- Statistics show Permanent count > 0, others = 0

#### TC4.4: Back to All Types
**Steps:**
1. Apply Change Type filter
2. Select "All Types" from dropdown

**Expected:**
- All logs reappear
- Statistics show full counts

---

### **Test Suite 5: Staff Name Filtering**

#### TC5.1: Exact Match
**Steps:**
1. Enter exact staff name from a log (e.g., "John Smith")
2. Observe results

**Expected:**
- Only logs with that exact staff name appear
- Case-insensitive match works
- Statistics update

#### TC5.2: Partial Match
**Steps:**
1. Enter partial name (e.g., "John")
2. Observe results

**Expected:**
- All logs with "John" in staff name appear
- Both "John Smith" and "Johnny Doe" would match
- Statistics update

#### TC5.3: Case Insensitivity
**Steps:**
1. Enter staff name in all caps (e.g., "JOHN SMITH")
2. Observe results

**Expected:**
- Matches regardless of case
- Results same as lowercase search

#### TC5.4: No Match
**Steps:**
1. Enter non-existent staff name (e.g., "XYZ999")
2. Observe results

**Expected:**
- Empty table with "No audit logs found" message
- Statistics show 0 for all counts

---

### **Test Suite 6: Combined Filters**

#### TC6.1: Date + Change Type
**Steps:**
1. Set date range: last 7 days
2. Select Change Type: "Temporary"
3. Observe results

**Expected:**
- Only temporary changes within date range appear
- Statistics reflect combined filter

#### TC6.2: All Filters Combined
**Steps:**
1. Set start date: 2025-11-15
2. Set end date: 2025-11-20
3. Select Change Type: "Return"
4. Enter staff name: "admin"
5. Observe results

**Expected:**
- Only logs matching ALL criteria appear
- Statistics update correctly
- If no matches, empty state shows

#### TC6.3: Clear All Filters
**Steps:**
1. Apply multiple filters
2. Click "Clear all filters" button
3. Observe results

**Expected:**
- All filter inputs reset to default
- All logs reappear
- Statistics show full counts
- Pagination resets to page 1

---

### **Test Suite 7: Pagination**

#### TC7.1: Page Navigation
**Steps:**
1. Load Audit Trail with >10 logs
2. Click "Next" button
3. Click "Previous" button

**Expected:**
- Next shows items 11-20
- Previous returns to items 1-10
- Page indicator updates (e.g., "Page 2 of 3")

#### TC7.2: First Page Boundary
**Steps:**
1. Ensure on page 1
2. Check "Previous" button state

**Expected:**
- "Previous" button is disabled
- Cannot go to page 0

#### TC7.3: Last Page Boundary
**Steps:**
1. Navigate to last page
2. Check "Next" button state

**Expected:**
- "Next" button is disabled
- Cannot exceed total pages

#### TC7.4: Pagination Reset on Filter
**Steps:**
1. Navigate to page 2
2. Apply any filter
3. Observe pagination

**Expected:**
- Automatically resets to page 1
- Shows first 10 filtered results

#### TC7.5: Single Page
**Steps:**
1. Apply filter that returns ≤10 results
2. Observe pagination controls

**Expected:**
- Shows "Page 1 of 1"
- Both Previous and Next disabled

---

### **Test Suite 8: CSV Export**

#### TC8.1: Export All Data
**Steps:**
1. Clear all filters
2. Click "Export CSV" button
3. Open downloaded file

**Expected:**
- CSV file downloads with filename: `audit-trail-YYYY-MM-DD.csv`
- Contains all logs
- Headers: Timestamp, Change Type, Old Vehicle, New Vehicle, Staff Name, Reason
- Data properly formatted and quoted

#### TC8.2: Export Filtered Data
**Steps:**
1. Apply filter (e.g., Change Type: "Temporary")
2. Click "Export CSV" button
3. Open downloaded file

**Expected:**
- CSV contains only filtered data
- Headers included
- Data matches what's visible in table (all pages, not just current page)

#### TC8.3: CSV Content Validation
**Steps:**
1. Export CSV
2. Open in Excel/Google Sheets
3. Verify formatting

**Expected:**
- Timestamps formatted as locale string
- Change types: TEMPORARY, RETURN, or PERMANENT
- Vehicle plates or "N/A" if null
- Staff names and reasons properly quoted
- No encoding issues with special characters

---

### **Test Suite 9: Print Functionality**

#### TC9.1: Print Dialog
**Steps:**
1. Click "Print" button (Printer icon)
2. Observe browser print dialog

**Expected:**
- Browser print dialog appears
- Print preview shows audit trail table
- Custom print styles applied (if any)

#### TC9.2: Print Filtered Data
**Steps:**
1. Apply filters
2. Click "Print" button
3. Check print preview

**Expected:**
- Print preview shows filtered data only
- Statistics visible in print preview
- Filters bar may or may not be visible (design decision)

---

### **Test Suite 10: UI/UX**

#### TC10.1: Color-Coded Badges
**Steps:**
1. Load Audit Trail
2. Observe change type badges

**Expected:**
- TEMPORARY: Blue background, blue text, Clock icon
- RETURN: Green background, green text, CheckCircle icon
- PERMANENT: Purple background, purple text, AlertCircle icon

#### TC10.2: Staff Avatars
**Steps:**
1. Observe staff name column
2. Check avatar circles

**Expected:**
- Each staff member has colored avatar circle
- Initials displayed (first letter of first and last name)
- Gradient background colors vary

#### TC10.3: Responsive Design
**Steps:**
1. Resize browser window to mobile size (< 768px)
2. Observe layout

**Expected:**
- Statistics cards stack vertically
- Filter inputs stack or wrap
- Table becomes scrollable or cards
- All features remain accessible

#### TC10.4: Hover Effects
**Steps:**
1. Hover over table rows
2. Hover over buttons

**Expected:**
- Row background changes on hover
- Buttons show hover state (darker background)
- Cursor changes to pointer where appropriate

---

### **Test Suite 11: Edge Cases**

#### TC11.1: Empty Database
**Steps:**
1. Clear all vehicle change logs from database
2. Reload Audit Trail

**Expected:**
- Statistics show 0 for all counts
- Empty state message: "No audit logs found. Changes will appear here once vehicles are modified."
- Filter controls still visible and functional
- No errors

#### TC11.2: Large Dataset
**Steps:**
1. Create 100+ vehicle change logs
2. Load Audit Trail
3. Test pagination and filters

**Expected:**
- Pagination works smoothly
- Statistics calculate correctly
- Filters perform adequately (no significant lag)
- CSV export includes all data

#### TC11.3: Very Long Text
**Steps:**
1. Create log with very long reason text (500+ characters)
2. Load Audit Trail

**Expected:**
- Text truncates or wraps properly
- Table layout doesn't break
- Full text visible on hover/expand (if implemented)
- CSV export includes full text

#### TC11.4: Null Values
**Steps:**
1. Create log with null oldVehiclePlate or newVehiclePlate
2. Load Audit Trail

**Expected:**
- "N/A" displayed instead of null
- No console errors
- CSV export shows "N/A"

#### TC11.5: Special Characters
**Steps:**
1. Create log with special characters in reason (e.g., quotes, commas, newlines)
2. Export to CSV

**Expected:**
- CSV properly escapes special characters
- Data displays correctly in UI
- No formatting issues

---

## Manual Testing Checklist

### Pre-Testing Setup
- [ ] Backend running on http://localhost:8080
- [ ] Frontend running on http://localhost:3001
- [ ] Logged in as admin user (admin/admin)
- [ ] Database has 12 sample activity logs (from DataLoader)

### Critical Path
- [ ] Audit Trail loads without errors
- [ ] Statistics cards show correct counts
- [ ] Date range filter works
- [ ] Change type filter works
- [ ] Staff name filter works
- [ ] Clear filters button works
- [ ] Pagination works (Next/Previous)
- [ ] CSV export downloads successfully
- [ ] Print dialog opens
- [ ] Refresh button reloads data

### Edge Cases
- [ ] Empty state displays when no data
- [ ] Error state displays when API fails
- [ ] Filters work when combined
- [ ] Pagination resets on filter change
- [ ] Export includes filtered data only
- [ ] No console errors during any operation

---

## Known Issues / Enhancement Opportunities

### Current Limitations
1. **No server-side pagination** - All data loaded at once (OK for small datasets)
2. **No date range validation** - End date can be before start date
3. **No advanced search** - Only basic text matching for staff names
4. **No sort options** - Data displayed in default order from API

### Potential Enhancements
1. Add date picker validation (end ≥ start)
2. Add column sorting (click headers to sort)
3. Add vehicle plate search filter
4. Add "Export to PDF" functionality
5. Add detailed view modal (click row to see full details)
6. Add keyboard shortcuts (e.g., Ctrl+P for print, Ctrl+E for export)
7. Add filter presets (e.g., "Last 7 days", "This month")
8. Add real-time updates (WebSocket/polling for new logs)

---

## API Contract

### Endpoint
```
GET /api/activities/all
```

### Headers
```
Authorization: Bearer <access_token>
```

### Response
```json
[
  {
    "id": "uuid",
    "checklistId": "uuid",
    "changeType": "TEMPORARY" | "RETURN" | "PERMANENT",
    "oldVehiclePlate": "ABC-123" | null,
    "newVehiclePlate": "XYZ-789" | null,
    "reason": "Customer requested different model",
    "timestamp": "2025-11-23T10:30:00",
    "staffName": "John Smith"
  }
]
```

### Error Responses
- **401 Unauthorized** - Invalid or missing JWT token
- **403 Forbidden** - User lacks permission
- **500 Internal Server Error** - Server-side error

---

## Success Criteria

The Audit Trail feature is considered **complete and working** when:

✅ All critical path tests pass
✅ No console errors during normal operation
✅ Data loads within 2 seconds for datasets <100 logs
✅ All filters work independently and combined
✅ CSV export produces valid, importable CSV files
✅ Print functionality works in major browsers
✅ UI is responsive on mobile and desktop
✅ Error states are handled gracefully

---

## Testing Status

| Test Suite | Status | Tester | Date | Notes |
|------------|--------|--------|------|-------|
| Data Loading | ⏳ Pending | - | - | - |
| Statistics Cards | ⏳ Pending | - | - | - |
| Date Range Filtering | ⏳ Pending | - | - | - |
| Change Type Filtering | ⏳ Pending | - | - | - |
| Staff Name Filtering | ⏳ Pending | - | - | - |
| Combined Filters | ⏳ Pending | - | - | - |
| Pagination | ⏳ Pending | - | - | - |
| CSV Export | ⏳ Pending | - | - | - |
| Print Functionality | ⏳ Pending | - | - | - |
| UI/UX | ⏳ Pending | - | - | - |
| Edge Cases | ⏳ Pending | - | - | - |

---

**Next Steps:**
1. Execute manual tests following this guide
2. Document any bugs found
3. Create automated tests (Vitest/Playwright) for critical paths
4. Update this document with test results
5. Mark test suites as ✅ Complete or ❌ Failed with notes

---

**Document maintained by:** Claude Code Assistant
**Last reviewed:** 2025-11-24
