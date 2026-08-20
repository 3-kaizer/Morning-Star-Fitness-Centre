# Implementation Plan - Firebase Realtime Database Integration

This plan outlines the steps to integrate Firebase Realtime Database (RTDB) into the Morning Star Fitness app for member data and attendance tracking, while preserving existing functionality and Firestore as a secondary storage if needed.

## User Review Required

> [!IMPORTANT]
> The integration will prioritize Realtime Database for all member and attendance operations. Firestore will be maintained for backward compatibility (dual-write during registration/profile updates), but the primary source of truth for the app will shift to RTDB.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/gradle/libs.versions.toml)
- Add `firebase-database` library definition.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/build.gradle.kts)
- Add `libs.firebase.database` dependency.

---

### Authentication & Registration

#### [MODIFY] [AuthViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/AuthViewModel.kt)
- Inject `FirebaseDatabase` instance.
- Update `createUser` to write member data to RTDB at `members/{uid}`.
- Ensure registration fails if RTDB write fails.
- Keep Firestore write as a secondary operation.

---

### Member Management

#### [MODIFY] [MemberViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/MemberViewModel.kt)
- Update `syncWithFirebase` to read from RTDB.
- Update `updateProfile` to write to RTDB (and Firestore).
- Update `ensureMembershipQr` to write to RTDB (and Firestore).

---

### Attendance Tracking

#### [MODIFY] [AttendanceViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/AttendanceViewModel.kt)
- Update `recordCheckIn` to write to RTDB at `members/{uid}/attendance/{yyyy-MM-dd}`.
- Prevent duplicate same-day attendance records using RTDB checks.
- Update `fetchAttendanceHistory` to read from RTDB.

---

### Verification Plan

### Automated Tests
- Run Gradle debug build: `./gradlew assembleDebug`
- Check for compilation errors.

### Manual Verification
1. **Registration**: Register a new member and verify data appears in RTDB `members/{uid}`.
2. **Login/Restore**: Log out and log back in, verify profile data is restored from RTDB.
3. **Attendance**: Record a check-in and verify it appears in RTDB `members/{uid}/attendance/{date}`. Try to check in again on the same day and ensure it's blocked.
4. **QR Code**: Verify the QR code is displayed correctly on the Check-in page with the expected format.
5. **UI Flow**: Navigate through Splash, Onboarding, Home, Profile, and Shop to ensure no regressions.
