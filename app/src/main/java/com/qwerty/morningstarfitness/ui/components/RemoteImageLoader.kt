package com.qwerty.morningstarfitness.ui.components

import android.content.Context
import coil3.ImageLoader
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * One app-wide Coil loader so remote images share the same memory/disk cache.
 * Preloading uses execute() rather than enqueue(), so the caller can wait for
 * the images to actually reach the cache before a swipe/navigation needs them.
 */
object RemoteImageLoader {
    fun create(context: Context): ImageLoader = ImageLoader.Builder(context.applicationContext)
        .build()

    suspend fun preload(
        context: Context,
        imageLoader: ImageLoader,
        urls: List<String>
    ) {
        val distinctUrls = urls.filter { it.isNotBlank() }.distinct()
        withContext(Dispatchers.IO) {
            coroutineScope {
                distinctUrls.map { url ->
                    async {
                        runCatching {
                            imageLoader.execute(
                                ImageRequest.Builder(context)
                                    .data(url)
                                    .memoryCacheKey(url)
                                    .diskCacheKey(url)
                                    .build()
                            )
                        }
                    }
                }.awaitAll()
            }
        }
    }
}
