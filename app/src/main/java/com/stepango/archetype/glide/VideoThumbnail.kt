package com.ninetyseconds.auckland.core.glide

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GenericLoaderFactory
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.stream.StreamModelLoader
import com.bumptech.glide.module.GlideModule
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class VideoThumbnail(val path: String)

class VideoThumbnailModel : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) = Unit

    override fun registerComponents(context: Context, glide: Glide) {
        glide.register(VideoThumbnail::class.java, InputStream::class.java, VideoThumbnailLoader.Factory())
    }
}

internal class VideoThumbnailLoader : StreamModelLoader<VideoThumbnail> {
    override fun getResourceFetcher(model: VideoThumbnail, width: Int, height: Int) = VideoThumbnailFetcher(model)

    internal class Factory : ModelLoaderFactory<VideoThumbnail, InputStream> {
        override fun build(context: Context, factories: GenericLoaderFactory) = VideoThumbnailLoader()

        override fun teardown() = Unit
    }
}

internal class VideoThumbnailFetcher(val model: VideoThumbnail) : DataFetcher<InputStream> {
    var stream: InputStream? = null
    @Volatile var cancelled = false

    override fun getId(): String = model.path

    override fun loadData(priority: Priority): InputStream? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(model.path, emptyMap())
            if (cancelled) return null
            val picture = retriever.frameAtTime
            if (cancelled) return null
            if (picture != null) {
                val bitmapData = ByteArrayOutputStream().use { bos ->
                    picture.compress(Bitmap.CompressFormat.JPEG, 90, bos)
                    bos.toByteArray()
                }
                if (cancelled) return null
                stream = ByteArrayInputStream(bitmapData)
                return stream
            }
        } finally {
            retriever.release()
        }
        return null
    }

    override fun cleanup() = try {
        stream?.close()
    } catch (e: IOException) {
        // Just Ignore it
    } ?: Unit

    override fun cancel() {
        cancelled = true
    }
}