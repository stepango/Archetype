package com.ninetyseconds.auckland.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource

class ColorFilterTransformation(private val mBitmapPool: BitmapPool, private val mColor: Int) : Transformation<Bitmap> {

    constructor(context: Context, color: Int) : this(Glide.get(context).bitmapPool, color)

    override fun transform(resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
        val source = resource.get()

        val width = source.width
        val height = source.height

        val config = if (source.config != null) source.config else Bitmap.Config.ARGB_8888
        var bitmap: Bitmap? = mBitmapPool.get(width, height, config)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, config)
        }

        val canvas = Canvas(bitmap!!)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_OVER)
        canvas.drawBitmap(source, 0f, 0f, paint)

        return BitmapResource.obtain(bitmap, mBitmapPool)
    }

    override fun getId() = "ColorFilterTransformation(color=$mColor)"

}