# Implementation Plan - Attendance History UI Overhaul & Integration

This plan outlines the redesign of the Attendance History screen to provide a more premium, "timeline-style" experience and its full integration with the `AttendanceViewModel` for real-time status updates.

## User Review Required

> [!IMPORTANT]
> The new UI will introduce a **Timeline View** which replaces the standard list items. It will also calculate and display **Monthly Stats** to give members better insight into their fitness consistency.

## Proposed Changes

### ViewModel Enhancements

#### [MODIFY] [AttendanceViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/AttendanceViewModel.kt)
- Add a helper function/property to calculate **Visits This Month**.
- Ensure `isLoading` and `loadError` are properly managed during data fetching.

---

### UI Redesign

#### [MODIFY] [AttendanceScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/attendance/AttendanceScreen.kt)
- **New `AttendanceHero` component**: A gradient-styled card showing total visits vs monthly visits.
- **New `TimelineItem` component**: Redesigned list items with a vertical timeline line, icons for check-in/out, and duration calculation.
- **Integrated States**: Handle `isLoading` and `loadError` with polished UI (shimmers/error views).
- **Pull-to-Refresh**: Add `PullToRefreshBox` for manual history updates.

---

### Navigation & Wiring

#### [MODIFY] [AppNavHost.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/navigation/AppNavHost.kt)
- Update the `ROUTE_ATTENDANCE` composable to pass `isLoading`, `loadError`, and the `onRefresh` callback from `AttendanceViewModel` to the `AttendanceScreen`.

---

## Verification Plan

### Automated Tests
- Run Gradle debug build: `./gradlew assembleDebug`
- Check for compilation errors in the new UI components.

### Manual Verification
1. **Initial Load**: Open History and verify the loading spinner/shimmer appears.
2. **Stats Check**: Verify "Total Visits" and "This Month" numbers match the history list.
3. **Timeline UI**: Verify the timeline line connects visits correctly and icons are aligned.
4. **Error Handling**: Temporarily disable network and verify the error state with a "Retry" button appears.
5. **Pull-to-Refresh**: Swipe down on the history list and verify data is re-fetched.
