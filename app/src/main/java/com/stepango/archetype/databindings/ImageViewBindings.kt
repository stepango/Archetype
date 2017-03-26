package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.stepango.archetype.player.di.Injector

@BindingAdapter(value = *arrayOf(
        "imageUrl",
        "imagePlaceholder",
        "imageCircle",
        "imageAnimate",
        "imageFitCenter",
        "imageColorFilter",
        "imageBorderWidth",
        "imageBorderColor"
), requireAll = false)
fun loadImage(view: ImageView, url: String?, @DrawableRes placeholder: Int,
              asCircle: Boolean?, animate: Boolean?, fitCenter: Boolean?, colorFilter: Int?,
              imageBorderWidth: Float?, @ColorRes imageBorderColor: Int?
) {
    url ?: return
    val imageLoader = Injector().imageLoader()
    imageLoader.load(url).apply {
        if (placeholder > 0) placeholder(placeholder)
        if (asCircle == true) asCircle()
        if (animate == true) animate()
        if (fitCenter == true) fitCenter()
        if (colorFilter != null) colorFilter(colorFilter)
        if (colorFilter != null) colorFilter(colorFilter)
        if (imageBorderColor != null) border(imageBorderColor, imageBorderWidth ?: 1f)
        else if (imageBorderWidth != null) border(Color.BLACK, imageBorderWidth)
        into(view)
    }
}