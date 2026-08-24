# Design Consistency Analysis

## Iconographic Inconsistency
You are correct; there is currently a mix of iconographic styles in the app, which can make the UI feel less "premium."

### Current State
1.  **System Navigation:** Using `androidx.compose.material.icons` (e.g., `ArrowBack`, `Visibility`). These are sharp, vector-based icons that follow Material Design.
2.  **Home Quick Actions:** Using standard Unicode Emojis (e.g., 🛍️, 👤, #).
    - **The Problem:** Emojis look different on every Android version and manufacturer skin (Samsung vs. Google vs. Xiaomi). They generally don't match the "Pulse Gym" high-performance, dark-mode aesthetic.

### Recommendation
We should replace the emojis in the `HomeScreen` with consistent **Material Design Icons**. This ensures that all icons share the same weight, stroke, and color palette (`PulseColors.TextPrimary` or `PulseColors.Accent`).

---

## Proposed UI Refactor

### [MODIFY] [HomeScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/home/HomeScreen.kt)
I propose changing the `QuickActionTile` to accept an `ImageVector` instead of a `String` (Emoji).

**Example Mapping:**
- **Shop:** `Icons.Default.ShoppingCart` (or `Storefront`)
- **Profile:** `Icons.Default.Person`
- **Attendance:** `Icons.Default.History` (or `CalendarMonth`)

### Consistency Checklist
- [ ] Use `PulseColors.TextPrimary` for inactive icons.
- [ ] Use `PulseColors.Accent` for action icons.
- [ ] Ensure all icons in the same package (e.g., the `QuickAction` row) have the same `size` (24dp is standard).

Would you like me to apply this refactor to the `HomeScreen` and `QuickActionTile` now?
