# Tasks for QR Entry Fix and Branding Overhaul

## 1. QR Entry Flow & Member Identification
- [ ] Update `MemberViewModel.kt`:
    - [ ] Change `ensureMembershipQr` to avoid auto-generating QRs if missing.
    - [ ] Add `getMembershipStatus()` helper.
    - [ ] Move `isExpired` logic to a central place or share it.
- [ ] Update `AppNavHost.kt`:
    - [ ] Pass full name and status to `QrEntryScreen`.
- [ ] Update `QrEntryScreen.kt`:
    - [ ] Display "MORNING STAR FITNESS CENTRE" header.
    - [ ] Display Member Name, ID, and Status.
    - [ ] Handle missing QR code with a clear error message.

## 2. Branding Overhaul
- [ ] Update `PulseComponents.kt`:
    - [ ] Update `BrandMark` text to "MORNING STAR FITNESS CENTRE".
- [ ] Update `strings.xml`:
    - [ ] Change `app_name` to "Morning Star Fitness Centre".
- [ ] Global Replacement:
    - [ ] `EntryScreen.kt`: "Pulse Gym" -> "Morning Star Fitness Centre".
    - [ ] `HomeScreen.kt`: Update header and branding.
    - [ ] `SuccessScreen.kt`: Branding.
    - [ ] `OrderSuccessScreen.kt`: Branding.
    - [ ] Check other screens (Login, Registration, Shop, etc.).

## 3. Verification & Deployment
- [ ] Verify `AttendanceViewModel` logic for daily check-in limits.
- [ ] Deploy to physical phone.
- [ ] Perform manual test of the entire QR entry and check-in flow.
- [ ] Final verification of all user-facing strings.
