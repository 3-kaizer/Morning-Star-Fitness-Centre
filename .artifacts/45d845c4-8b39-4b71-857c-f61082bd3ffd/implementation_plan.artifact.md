# Implementation Plan - Fix Kotlin Version Mismatch and RegistrationViewModel Error

The project is failing to build due to a Kotlin version mismatch (`kotlin-stdlib:2.4.0` vs compiler `2.2.0`) and a missing parameter error in `RegistrationViewModel.kt`.

## User Review Required

> [!IMPORTANT]
> The plan involves forcing all Kotlin dependencies to version `2.2.0` using a `resolutionStrategy`. This will ensure consistency even if transitive dependencies request a newer version.

## Proposed Changes

### [Build Configuration]

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/build.gradle.kts)
- Add a `configurations.configureEach` block with a `resolutionStrategy` to force `org.jetbrains.kotlin` group dependencies to version `2.2.0`.
- Verify that no direct `2.4.0` declarations exist (none found in initial search).

### [Registration Logic]

#### [MODIFY] [RegistrationViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/RegistrationViewModel.kt)
- Update the `persist()` function to pass `securityQuestion` and `securityAnswer` to `store.saveMember()`.
- These values will be sourced from the `memberForm` (of type `MemberFormState`).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:dependencyInsight --dependency kotlin-stdlib --configuration debugCompileClasspath` (via `gradle_build` tool) to verify that `kotlin-stdlib` resolves to `2.2.0`.
- Run `./gradlew assembleDebug` to ensure the project builds successfully.

### Manual Verification
- Once the build passes, I will verify the `RegistrationViewModel.kt` changes by ensuring the code compiles.
