# Implementation Plan - Automatic Cloudinary Image Upload

This plan removes the manual URL pasting workaround and replaces it with an automatic image upload flow. When a user picks a photo from their gallery, it will be uploaded to Cloudinary, and the resulting public URL will be used for their profile.

## User Review Required

> [!NOTE]
> **Cloudinary Config Received:**
> - **Cloud Name:** `chxq0j6j`
> - **Upload Preset:** `android` (Unsigned)

## Proposed Changes

### 1. Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/build.gradle.kts)
- Add `CLOUDINARY_CLOUD_NAME` and `CLOUDINARY_UPLOAD_PRESET` to `buildConfigField` in `defaultConfig`.

### 2. Upload Service

#### [NEW] [CloudinaryUploader.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/utils/CloudinaryUploader.kt)
- Create a utility class that uses the existing `OkHttp` client to perform multipart POST requests to the Cloudinary Upload API (`https://api.cloudinary.com/v1_1/${cloudName}/image/upload`).
- Implement a `uploadImage(context: Context, uri: Uri)` function.

### 3. UI Updates

#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/ui/screens/profile/ProfileScreen.kt)
- **Remove** "Cloudinary Integration" section.
- **Remove** "Profile image URL" FormField.
- **Add** an `isUploading` state to show a loading spinner over the profile picture during the upload process.
- **Trigger** the upload immediately after the user selects an image from the gallery using `rememberCoroutineScope`.
- **Update** the `draft` state with the returned Cloudinary URL once the upload is successful.
- **Handle Errors:** Show a Toast if the upload fails.

### 3. ViewModel Integration

#### [MODIFY] [MemberViewModel.kt](file:///C:/Users/ADMIN/StudioProjects/Morning-Star-Fitness-Centre/app/src/main/java/com/qwerty/morningstarfitness/viewmodels/MemberViewModel.kt)
- Add a helper function if needed, or simply let `ProfileScreen` handle the upload before calling `onSave`.

---

## Verification Plan

### Automated Tests
- Run Gradle build to ensure no syntax errors.

### Manual Verification
1. Open Profile.
2. Tap the camera icon.
3. Select an image from the device gallery.
4. Verify a loading indicator appears on the profile photo.
5. Verify the photo updates to the selected image once the loading finishes.
6. Click "SAVE PROFILE".
7. Log out and log back in (or check Firebase) to ensure the Cloudinary URL was saved and is correctly retrieved.
