package com.qwerty.morningstarfitness.ui.screens.launch

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Shared cached image loader for onboarding photography. */
object OnboardingImageLoader {
    private const val DISK_CACHE_SIZE = 50L * 1024L * 1024L

    fun create(context: Context): ImageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("onboarding_images"))
                .maxSizeBytes(DISK_CACHE_SIZE)
                .build()
        }
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
