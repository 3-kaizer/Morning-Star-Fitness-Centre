package com.qwerty.morningstarfitness.ui.screens.launch

import android.content.Context
import coil3.ImageLoader
import com.qwerty.morningstarfitness.ui.components.RemoteImageLoader

/** Backwards-compatible onboarding facade over the app-wide image cache. */
object OnboardingImageLoader {
    fun create(context: Context): ImageLoader = RemoteImageLoader.create(context)

    suspend fun preload(context: Context, imageLoader: ImageLoader, urls: List<String>) {
        RemoteImageLoader.preload(context, imageLoader, urls)
    }
}
