package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.view.View
import com.ninetyseconds.auckland.core.widget.visible

@BindingAdapter("visible")
fun visible(v: View, visible: Boolean) {
    synchronized(v) {
        v.visible = visible
    }
}

@BindingAdapter("requestFocus")
fun requestFocus(v: View, focus: Boolean) {
    if (focus) v.post { v.requestFocus() }
}