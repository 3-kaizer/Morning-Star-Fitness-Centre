package com.qwerty.morningstarfitness.ui.screens.launch

import android.content.Context
import coil3.ImageLoader
import coil3.request.ImageRequest

/** Shared Coil loader used to warm the onboarding image cache. */
object OnboardingImageLoader {
    fun create(context: Context): ImageLoader = ImageLoader.Builder(context).build()

    fun preload(context: Context, imageLoader: ImageLoader, urls: List<String>) {
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
