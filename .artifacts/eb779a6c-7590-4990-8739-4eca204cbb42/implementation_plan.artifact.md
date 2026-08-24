# Fix Kotlin Version Mismatch and Build Errors

The project is experiencing build failures due to a mismatch between the Kotlin compiler version (2.2.0) and the resolved Kotlin standard library version (2.4.0). This causes basic Kotlin functions to be unresolved.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/gradle/libs.versions.toml)
- Align `kotlin` version to `2.2.0`.
- Add `kotlin-android` plugin definition.
- Add `kotlin-stdlib` library definition.

#### [MODIFY] [build.gradle.kts (root)](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/build.gradle.kts)
- Add `alias(libs.plugins.kotlin.android) apply false` to the plugins block.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/build.gradle.kts)
- Apply `alias(libs.plugins.kotlin.android)`.
- Add explicit dependency on `libs.kotlin.stdlib`.
- Add a dependency constraint to force Kotlin standard library and related artifacts to `2.2.0`.

### Source Code Fixes

#### [MODIFY] [AppNavHost.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/navigation/AppNavHost.kt)
- Verify if any missing imports or unresolved references remain after the Kotlin version fix.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to verify the build.

### Manual Verification
- Re-sync the project in Android Studio and check for lint errors in `AppNavHost.kt`.
- Verify that `listOf`, `split`, `let`, etc., are correctly resolved.
