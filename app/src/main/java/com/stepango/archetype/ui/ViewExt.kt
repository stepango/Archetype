package com.stepango.archetype.ui

import android.view.View

fun View.stopAnimation() = apply { clearAnimation(); animate().cancel() }
fun View.show() = apply { visibility = View.VISIBLE }
fun View.gone() = apply { visibility = View.GONE }
fun View.invisible() = apply { visibility = View.INVISIBLE }
var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (value) show() else gone()
    }