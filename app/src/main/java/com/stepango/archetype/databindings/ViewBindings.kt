package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.view.View
import com.stepango.archetype.ui.visible

@BindingAdapter("visible")
fun visible(v: View, visible: Boolean) {
    v.visible = visible
}

@BindingAdapter("requestFocus")
fun requestFocus(v: View, focus: Boolean) {
    if (focus) v.post { v.requestFocus() }
}