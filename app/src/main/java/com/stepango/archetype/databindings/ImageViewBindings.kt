package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.stepango.archetype.player.di.Injector

@BindingAdapter(value = *arrayOf(
        "imageUrl",
        "imagePlaceholder",
        "imageCircle"
), requireAll = false)
fun loadImage(view: ImageView, url: String?, @DrawableRes placeholder: Int, asCircle: Boolean?) {
    url ?: return
    val imageLoader = Injector().imageLoader()
    imageLoader.load(url).apply {
        if (placeholder > 0) placeholder(placeholder)
        if (asCircle == true) asCircle()
        into(view)
    }
}