package com.ninetyseconds.auckland.core.widget

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

inline fun <T : ViewGroup.LayoutParams> View.updateLayoutParams(block: (T) -> Unit) {
    @Suppress("UNCHECKED_CAST")
    block(layoutParams as T)
    postOnAnimation { requestLayout() }
}

fun View.stopAnimation() = apply { clearAnimation(); animate().cancel() }
fun View.show() = apply { visibility = View.VISIBLE }
fun View.gone() = apply { visibility = View.GONE }
fun View.invisible() = apply { visibility = View.INVISIBLE }
var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (value) show() else gone()
    }

fun TextView.setFlag(flag: Int, enable: Boolean) = apply {
    paintFlags = if (enable) paintFlags or flag else paintFlags and flag.inv()
}
