# Fix QR Entry Flow and Branding for Morning Star Fitness Centre

This plan addresses the QR code display issue on the entry screen, ensures correct check-in logic, and removes all "Pulse Gym" branding from the user interface.

## Proposed Changes

### 1. QR Entry Flow & Member Identification

#### [MODIFY] [MemberViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/MemberViewModel.kt)
- Update `ensureMembershipQr` to return the existing `qrCodeValue` without generating a new one if it's null.
- Add a helper function/property to determine membership status (e.g., "Active", "Expired", "Pending").
- Ensure `syncWithFirebase` correctly populates all fields.

#### [MODIFY] [AppNavHost.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/navigation/AppNavHost.kt)
- Pass the member's full name and membership status to `QrEntryScreen`.
- Ensure the authenticated user's data is loaded before navigating to the QR screen if possible, or handle loading state in `QrEntryScreen`.

#### [MODIFY] [QrEntryScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/entry/QrEntryScreen.kt)
- Update UI to display:
    - **MORNING STAR FITNESS CENTRE** (Header)
    - **Member: [Full Name]**
    - **Member ID: [ID]**
    - **Status: [Active / Expired]**
- If `qrCodeValue` is missing, show a clear error message: "Member QR code is unavailable. Please contact the front desk." instead of a fake QR or "Pending".

### 2. Branding Overhaul

#### [MODIFY] [PulseComponents.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/components/PulseComponents.kt)
- Update `BrandMark` to display "MORNING STAR FITNESS CENTRE".
- Rename internal components or references where appropriate (though `PulseColors` can stay as an internal design token name).

#### [MODIFY] [strings.xml](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/res/values/strings.xml)
- Update `app_name` to "Morning Star Fitness Centre".

#### [MODIFY] [Global String Scan]
- Scan and replace all user-facing occurrences of "Pulse Gym", "PULSE GYM", "PulseGym" with "Morning Star Fitness Centre" or "Morning Star" as appropriate for the context.
- Affected files likely include: `EntryScreen.kt`, `HomeScreen.kt`, `SuccessScreen.kt`, `OrderSuccessScreen.kt`, etc.

### 3. Check-in Flow Verification

- Verify that `AttendanceViewModel.recordCheckIn()` correctly:
    - Prevents multiple check-ins per day.
    - Updates the visit count (attendance history size).
    - Writes to the correct Firebase path for the authenticated member.

## Verification Plan

### Automated Tests
- Not applicable for this UI-heavy/Firebase-integrated fix, but I will perform manual verification.

### Physical Phone Test
1. **Login** as an existing member.
2. Navigate to **Entry Screen**.
3. Press **ENTER THE GYM**.
4. Verify **Member QR Code** appears with correct name, ID, and status.
5. Press **RECORD VISIT** and verify:
    - Success message appears.
    - Subsequent clicks on "RECORD VISIT" are blocked or show "already checked in".
    - Visit count on Home screen updates.
    - Attendance history updates.
6. **Global Search** for "Pulse" to ensure no user-facing branding remains.
