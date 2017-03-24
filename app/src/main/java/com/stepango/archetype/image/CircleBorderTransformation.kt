package com.ninetyseconds.auckland.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource


class CircleBorderTransformation(
        private val bitmapPool: BitmapPool,
        val strokeWidth: Float,
        val color: Int
) : Transformation<Bitmap> {

    constructor(context: Context,
                strokeWidth: Float,
                color: Int) : this(Glide.get(context).bitmapPool, strokeWidth, color)

    override fun transform(resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
        val source = resource.get()

        val width = source.width
        val height = source.height

        val config = if (source.config != null) source.config else Bitmap.Config.ARGB_8888
        val bitmap = bitmapPool.get(width, height, config) ?: Bitmap.createBitmap(width, height, config)

        val canvas = Canvas(bitmap!!)
        drawCircleBorder(canvas, height, width)
        drawImage(canvas, height, width, source)

        return BitmapResource.obtain(bitmap, bitmapPool)
    }

    private fun drawCircleBorder(canvas: Canvas, height: Int, width: Int) {
        val circlePaint = Paint()
        circlePaint.isAntiAlias = true
        //because we need draw background for transparent icons
        circlePaint.style = Paint.Style.FILL
        circlePaint.color = color
        val offset = strokeWidth / 2f
        val rect = RectF(0f + offset, 0f + offset, width.toFloat() - offset, height.toFloat() - offset)
        canvas.drawOval(rect, circlePaint)
    }

    private fun drawImage(canvas: Canvas, height: Int, width: Int, source: Bitmap) {
        val newWidth = width - strokeWidth * 2
        val newHeight = height - strokeWidth * 2
        val m = Matrix()
        m.setScale(newWidth / source.width, newHeight / source.height)
        m.postTranslate(strokeWidth, strokeWidth)
        canvas.drawBitmap(source, m, Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG))
    }

    override fun getId() = "CircleBorderTransformation()"
}