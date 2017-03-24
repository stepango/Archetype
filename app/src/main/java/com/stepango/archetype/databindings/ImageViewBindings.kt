package com.ninetyseconds.auckland.core.databindings

import android.databinding.BindingAdapter
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ninetyseconds.auckland.core.glide.VideoThumbnail
import com.ninetyseconds.auckland.di.Injector

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
    val imageLoader = Injector().imageLoader
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

@BindingAdapter("videoThumbnail")
fun videoThumbnail(view: ImageView, path: String) {
    //TODO wrap into image loader
    Glide.with(view.context).load(VideoThumbnail(path)).centerCrop().into(view)
}

@BindingAdapter("srcCompat")
fun srcCompat(view: ImageView, id: Int) {
    view.setImageDrawable(VectorDrawableCompat.create(view.context.resources, id, view.context.theme))
}
