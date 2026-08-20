# Firebase Data Sync Issues

The issue where users are appearing in **Authentication** but not in the **Database** is due to a configuration mismatch in the Firebase Console and the app's implementation.

## 🔍 Diagnosis
Based on the device logs, the app is attempting to use **Cloud Firestore**, but the database has not been initialized in your Firebase project.

> [!WARNING]
> **Firestore Log Error:**
> `The database (default) does not exist for project morning-star-6c5e6. Please visit the Firebase console to add a Cloud Firestore database.`

## 🛠️ Solutions

### Option 1: Enable Cloud Firestore (Recommended)
If you want to keep using the current code, you just need to activate Firestore in the console:
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Select your project: `morning-star-6c5e6`.
3. In the left sidebar, click **Build** > **Firestore Database**.
4. Click **Create Database**.
5. Select a location and choose **Start in test mode** (for development).
6. Click **Enable**.

### Option 2: Switch to Realtime Database
If you specifically intended to use the **Realtime Database** (the one with the JSON-like tree), we need to update the app's code and dependencies.

#### 1. Add Dependency
In `gradle/libs.versions.toml`:
```toml
[libraries]
firebase-database = { module = "com.google.firebase:firebase-database" }
```

In `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation(libs.firebase.database)
}
```

#### 2. Update Code
Modify `RegistrationViewModel.kt` to use `FirebaseDatabase`:
```kotlin
// Change imports
import com.google.firebase.database.FirebaseDatabase

// Change initialization
private val database = FirebaseDatabase.getInstance().reference

// Change save logic
database.child("members").child(uid).setValue(memberData).await()
```

## 📝 Recommendation
**Stick with Cloud Firestore.** It is the newer, more powerful database from Firebase and is what your code is already written for. Once you click "Create Database" in the console, your users will start appearing immediately.
