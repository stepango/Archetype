package com.stepango.archetype.image

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.DrawableRes
import android.widget.ImageView

/**
 * Simple image loader interface that encapsulates features we need now
 */
interface ImageLoader {

    /**
     * Loads from provided url, path or uri represented as String
     * @param url path to image
     * @return configurable Request class implementation
     */
    fun load(url: String?): Request

    /**
     * Loads from provided drawableId

     * @param drawableId - id of drawable
     * *
     * @return configurable Request class implementation
     */
    fun load(@DrawableRes drawableId: Int): Request

    /**
     * Configurable image loading Request class made with Builder pattern
     * Descendants must implement several `into` methods using user-provided configuration
     */
    abstract class Request(protected var url: String? = null, @DrawableRes protected var drawableId: Int = 0) {
        protected var isAsCircle: Boolean = false
        protected var borderParams: BorderRequestParams? = null
        protected var animate = false
        protected var centerCrop = true
        protected var colorFilter: Int? = null
        @DrawableRes protected var placeholderId: Int = 0
        @DrawableRes protected var errorId: Int = 0

        fun placeholder(@DrawableRes placeholderId: Int): Request = apply {
            this.placeholderId = placeholderId
        }

        fun error(@DrawableRes errorId: Int): Request = apply { this.errorId = errorId }

        fun asCircle(): Request = apply { isAsCircle = true }

        fun animate(): Request = apply { animate = true }

        fun fitCenter(): Request = apply { centerCrop = false }

        fun colorFilter(color: Int) = apply { colorFilter = color }

        fun border(color: Int, width: Float) = apply { borderParams = BorderRequestParams(color, width) }

        abstract fun into(imageView: ImageView)

        abstract fun bitmap(context: Context, width: Float, height: Float): Bitmap
    }

    class BorderRequestParams(
            val color: Int,
            val width: Float
    )

}
