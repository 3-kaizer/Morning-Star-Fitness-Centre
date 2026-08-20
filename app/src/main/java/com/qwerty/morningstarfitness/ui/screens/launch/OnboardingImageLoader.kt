package com.qwerty.morningstarfitness.ui.screens.launch

import android.content.Context
import coil3.ImageLoader
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Shared cached image loader for onboarding photography. */
object OnboardingImageLoader {
    fun create(context: Context): ImageLoader = ImageLoader.Builder(context)
        .build()

    suspend fun preload(context: Context, imageLoader: ImageLoader, urls: List<String>) {
        withContext(Dispatchers.IO) {
            urls.forEach { url ->
                imageLoader.enqueue(
                    ImageRequest.Builder(context)
                        .data(url)
                        .memoryCacheKey(url)
                        .diskCacheKey(url)
                        .build()
                )
            }
        }
    }
}
