# Implementation Plan - Enhanced Shop Order System

Improve the Shop order system to record complete customer identity, fulfilment details, and simulated M-Pesa payment status.

## User Review Required

> [!IMPORTANT]
> The payment system remains a **DEMO/SANDBOX** simulator. No real money will be transferred.
> Orders will be stored under both a global `orders` node and the user's `members/UID/orders` node for easy retrieval.

## Proposed Changes

### Data Models
#### [MODIFY] [OrderModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/models/OrderModel.kt)
- Add customer snapshot fields: `customerName`, `customerPhone`, `customerEmail`.
- Add payment fields: `paymentMethod`, `paymentStatus`, `paidAt`, `demoReceipt`.
- Add fulfilment fields: `fulfilmentMethod`, `deliveryLocation`, `deliveryAddress`.
- Update `OrderItem` to match the required structure.

### ViewModels
#### [MODIFY] [ShopViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/ShopViewModel.kt)
- Update `placeOrder` logic to calculate totals correctly.
- Add support for fulfilment selection (pickup/delivery).
- Add validation for delivery details.
- Integrate with `MpesaPaymentViewModel`.

#### [MODIFY] [MpesaPaymentViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/MpesaPaymentViewModel.kt)
- Add `shop_order` case to `simulateSuccessfulPayment`.
- Ensure it updates the order status in Firebase upon success.

### UI Screens
#### [MODIFY] [ShopScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/shop/ShopScreen.kt)
- Implement a multi-step checkout: Cart -> Checkout Summary/Payment -> Success.
- Add UI for selecting fulfilment method.
- Add input fields for delivery location and address.
- Show detailed order summary and demo payment instructions.

#### [NEW] [OrderSuccessScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/shop/OrderSuccessScreen.kt)
- Display successful order details as specified.

#### [MODIFY] [AppNavHost.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/navigation/AppNavHost.kt)
- Wire up the new screens and states.

## Verification Plan

### Automated Tests
- N/A (Manual verification on physical device required)

### Manual Verification
1. **Startup**: Build and install on physical phone.
2. **Shop Flow (Pickup)**: Add items -> Select Pickup -> Simulate Payment -> Verify Firebase Record.
3. **Shop Flow (Delivery)**: Add items -> Select Delivery -> Enter Address -> Simulate Payment -> Verify Firebase Record.
4. **Cancellation**: Start checkout -> Cancel Payment -> Verify no "paid" order is created.
5. **Validation**: Attempt delivery without address -> Verify button is disabled/error shown.
6. **Regression**: Check Membership Registration/Renewal to ensure payment simulator still works there.
