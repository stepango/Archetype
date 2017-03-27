package com.stepango.archetype.glide

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.GenericRequestBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.stepango.archetype.image.CropCircleTransformation
import com.stepango.archetype.image.ImageLoader
import com.stepango.archetype.util.dp
import java.util.ArrayList

class GlideImageLoader : ImageLoader {

    override fun load(url: String?): Request = Request(url = url)
    override fun load(drawableId: Int): Request = Request(drawableId = drawableId)

    class Request @JvmOverloads constructor(
            url: String? = null,
            drawableId: Int = 0
    ) : ImageLoader.Request(url, drawableId) {

        override fun bitmap(context: Context, width: Float, height: Float): Bitmap
                = buildBitmapRequest(context).into(width.dp(context), height.dp(context)).get()

        override fun into(imageView: ImageView) {
            buildDrawableRequest(imageView.context).into(imageView)
        }

        private fun buildBitmapRequest(ctx: Context) = makeRequest(ctx).asBitmap().apply {
            applyTransformations(ctx).apply { if (isNotEmpty()) transform(*this) }
        }


        private fun buildDrawableRequest(ctx: Context) = makeRequest(ctx).apply {
            applyTransformations(ctx).apply { if (isNotEmpty()) bitmapTransform(*this) }
        }

        private fun makeRequest(context: Context) = Glide.with(context).let {
            when {
                url != null    -> it.load(url)
                drawableId > 0 -> it.load(drawableId)
                else           -> throw IllegalArgumentException()
            }
        }

        private fun GenericRequestBuilder<*, *, *, *>.applyTransformations(ctx: Context): Array<Transformation<Bitmap>> {
            diskCacheStrategy(DiskCacheStrategy.ALL)
            val transformations: MutableList<Transformation<Bitmap>> = ArrayList()
            if (placeholderId > 0) placeholder(ContextCompat.getDrawable(ctx, placeholderId))
            if (!isAsCircle) {
                transformations += FitCenter(ctx)
            }
            if (isAsCircle) transformations += CropCircleTransformation(ctx)
            return transformations.toTypedArray()
        }
    }

}