# Morning Star Fitness Centre - Audit & Restructure Plan

This plan outlines the complete restructuring of the Morning Star Fitness Centre project to use a **Local Node.js Backend** for M-Pesa payments, keeping Firebase on the **Free Spark Plan**, and improving core membership/shop features.

## User Review Required

> [!IMPORTANT]
> **Firebase Downgrade**: I will remove all Firebase Cloud Functions code and dependencies to ensure the project stays on the Spark plan.
> **Local Backend**: You will need to run the Node.js server locally on your laptop (`mpesa-server`).
> **ngrok**: To receive M-Pesa callbacks, you must use `ngrok` to expose your local port 3000 to the internet.

## Proposed Changes

### 1. Backend: Local Node.js Server (`mpesa-server`)
#### [NEW] [mpesa-server/package.json](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/mpesa-server/package.json)
- Dependencies: `express`, `axios`, `dotenv`, `cors`, `firebase-admin`.
#### [NEW] [mpesa-server/index.js](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/mpesa-server/index.js)
- `POST /api/mpesa/stkpush`: Generates token and triggers Daraja STK Push.
- `POST /api/mpesa/callback`: Receives M-Pesa confirmation and updates Firebase RTDB.
- `GET /health`: Health check endpoint.
#### [NEW] [mpesa-server/.env.example](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/mpesa-server/.env.example)
- Template for Daraja credentials.

### 2. Android App: ViewModel Refactoring
#### [MODIFY] [RegistrationViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/RegistrationViewModel.kt)
- Remove `FirebaseFunctions`.
- Replace `initiateMpesaPayment` with a `Retrofit` or `HttpURLConnection` call to `http://localhost:3000/api/mpesa/stkpush`.
#### [MODIFY] [MemberViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/MemberViewModel.kt)
- Implement Renewal logic: Extend expiry if active, start from today if expired.
- Call local backend for M-Pesa.
#### [MODIFY] [ShopViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/ShopViewModel.kt)
- Implement Delivery vs Pickup logic.
- Call local backend for M-Pesa.

### 3. Android App: UI Improvements
#### [MODIFY] [HomeScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/home/HomeScreen.kt)
- Redesign dashboard: Membership status (Active/Expired), days remaining, expiry date, visits count.
- Add quick shortcuts to Shop, Profile, and QR.
#### [MODIFY] [PaymentScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/payment/PaymentScreen.kt)
- Enforce M-Pesa only. Remove all other options.
- Add "Sandbox Demo" indicators.
#### [MODIFY] [ShopScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/shop/ShopScreen.kt)
- Update product list with realistic gym items.
- Add Delivery address collection.

### 4. Cleanup & Security
- Remove `functions` folder.
- Remove Firebase Functions SDK from `app/build.gradle.kts`.
- Update `.gitignore` to include `mpesa-server/.env`.
- Update `database.rules.json` to prevent users from self-activating memberships.

## Verification Plan

### Automated Tests
- Build Android App: `./gradlew assembleDebug`
- Backend Check: `node index.js` and test `/health`.

### Manual Verification
- **DEMO 1**: Registration + Successful M-Pesa payment -> Active.
- **DEMO 2**: Cancel M-Pesa -> Not Active.
- **DEMO 3**: Renewal (Active member) -> Expiry extended.
- **DEMO 4**: Shop order with Delivery + Payment verification.
- **DEMO 5**: Attendance check verification.
