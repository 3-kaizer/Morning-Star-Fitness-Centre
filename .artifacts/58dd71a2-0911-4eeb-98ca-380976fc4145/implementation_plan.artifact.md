# Robust Registration & Payment Implementation

Ensure that membership registration and payment processing are atomic and properly handle errors before navigating to the success screen.

## User Review Required

> [!IMPORTANT]
> The account creation (Firebase Auth) will now happen during the "Confirm Payment" step. If this fails (e.g., account already exists), the user will remain on the payment screen with an error message instead of being incorrectly directed to the success screen.

## Proposed Changes

### ViewModel & Data Layer

#### [MODIFY] [RegistrationViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/RegistrationViewModel.kt)
- Add `registrationError` state to track failures.
- Refactor `processPayment` to be `suspend` and return a `Boolean` (or `Result`) indicating success.
- Update `saveToFirebase` to be a `suspend` function that throws or returns success, rather than launching its own coroutine.
- Ensure `isProcessingPayment` remains true until the entire cloud registration is complete.

---

### UI Layer

#### [MODIFY] [PaymentScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/payment/PaymentScreen.kt)
- Add `isProcessing` and `errorMessage` parameters to the `Composable`.
- Update the UI to show a loading state (e.g., changing button text to "Processing...") when `isProcessing` is true.
- Display the `errorMessage` if registration fails.
- Disable interactions while a payment is being processed.

#### [MODIFY] [AppNavHost.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/navigation/AppNavHost.kt)
- Update the `PaymentScreen` call to pass the new state variables (`isProcessingPayment`, `registrationError`).
- Use a `CoroutineScope` within `onConfirm` to call the now-suspend `processPayment` function.
- Only navigate to `ROUTE_SUCCESS` if `processPayment` returns success.

## Verification Plan

### Manual Verification
1.  **Success Path:** Complete registration and payment. Verify the app waits on the payment screen until account creation is confirmed, then navigates to Success.
2.  **Failure Path (Duplicate Account):** Attempt registration with an email already in use. Verify the app shows an error on the payment screen and does NOT navigate to the success screen.
3.  **UI Feedback:** Verify the "Confirm payment" button is disabled and shows a loading state during the process.
