# Implementation Plan: Navigation and Registration Screen Setup

This plan outlines the steps to organize the registration screen code, set up the navigation architecture, and add the required dependencies to the Morning Star Fitness app.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///home/emobilis/AndroidStudioProjects/MorningStarfitness/gradle/libs.versions.toml)
- Add `navigation-compose` version and library definition.

#### [MODIFY] [build.gradle.kts](file:///home/emobilis/AndroidStudioProjects/MorningStarfitness/app/build.gradle.kts)
- Add the Navigation Compose dependency.

---

### UI Organization

#### [NEW] [RegistrationScreen.kt](file:///home/emobilis/AndroidStudioProjects/MorningStarfitness/app/src/main/java/com/qwerty/morningstarfitness/ui/theme/screens/registration/RegistrationScreen.kt)
- Move `RegistrationScreen`, `MemberFormState`, and UI helper functions from `PulseColors.kt` to this new file.

#### [MODIFY] [PulseColors.kt](file:///home/emobilis/AndroidStudioProjects/MorningStarfitness/app/src/main/java/com/qwerty/morningstarfitness/ui/theme/screens/registration/PulseColors.kt)
- Remove the Composables and data classes, leaving only the `PulseColors` object and `DisplayFont` definition.

---

### Navigation

#### [MODIFY] [Routes.kt](file:///home/emobilis/AndroidStudioProjects/MorningStarfitness/app/src/main/java/com/qwerty/morningstarfitness/navigation/Routes.kt)
- Define the navigation routes (e.g., `ROUTE_REGISTRATION`).

#### [MODIFY] [AppNavHost.kt](file:///home/emobilis/AndroidStudioProjects/MorningStarfitness/app/src/main/java/com/qwerty/morningstarfitness/navigation/AppNavHost.kt)
- Implement the `NavHost` to manage screen transitions.

#### [MODIFY] [MainActivity.kt](file:///home/emobilis/AndroidStudioProjects/MorningStarfitness/app/src/main/java/com/qwerty/morningstarfitness/MainActivity.kt)
- Set `AppNavHost` as the root content of the app.

## Verification Plan

### Automated Tests
- Run `./gradlew build` to ensure the project compiles with new dependencies and refactored code.

### Manual Verification
- Deploy the app to a device/emulator to verify that the Registration screen is displayed as the initial screen.
- Verify form validation logic by attempting to "Continue" with empty fields.
