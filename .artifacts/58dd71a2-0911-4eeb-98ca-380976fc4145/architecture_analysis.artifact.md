# Architecture & Product Data Analysis

## 1. Single ViewModel Assessment
Currently, `RegistrationViewModel` is acting as a "Global" or "Shared" ViewModel.

### Is it okay?
For the current scale of the app, it works, but it's becoming a **God Object**. It handles:
- **Authentication** (Firebase Auth)
- **Member Registration** (Local & Remote)
- **Profile Management**
- **Persistence** (DataStore)
- **Attendance Logging** (Firestore)

### Recommendation: Split the ViewModels
To follow Clean Architecture and ensure maintainability, we should split these into:
1. **`AuthViewModel`**: Login, Sign up, Security Questions.
2. **`MemberViewModel`**: Profile data, Membership status, QR Code.
3. **`AttendanceViewModel`**: Check-in logic and history sync.
4. **`ShopViewModel`**: Product listing, cart management, and order processing.

---

## 2. Product Data Issues
I've identified several issues with the "product data" implementation in the `shop` package:

### A. State Persistence
In `ShopScreen.kt`, the cart is stored using `remember { mutableStateOf(...) }`.
- **The Problem:** If a user adds items to their cart, navigates to the Profile, and then goes back to the Shop, **the cart is cleared**.
- **The Fix:** Move the cart state to a ViewModel.

### B. Static Data
Products are hardcoded in `ProductData.kt`.
- **The Problem:** You cannot update the inventory, prices, or images without a new app release.
- **The Fix:** Move `defaultProducts` to Firestore and fetch them in a `ShopViewModel`.

### C. Missing Order Logic
The "Place Order" button doesn't actually record an order in any database.
- **The Problem:** The gym management has no record of what the user "ordered".
- **The Fix:** Create an `orders` collection in Firestore and save the cart contents there when "Place Order" is clicked.

### D. Image Loading (Coil 3)
The use of `coil3.compose.AsyncImage` with `loremflickr` URLs is fine for testing, but in production, you'll want to ensure the `imageUrl` field in `ProductModel` is handled as an optional or provided with a placeholder.

## Proposed Strategy
1.  **Create `ShopViewModel`**: To manage the cart and product list.
2.  **Refactor `AppNavHost`**: To inject the `ShopViewModel`.
3.  **Sync Products**: (Optional but recommended) Move the `defaultProducts` list to a Firestore collection.

Would you like me to start by splitting the ViewModels or focusing on fixing the Shop logic specifically?
