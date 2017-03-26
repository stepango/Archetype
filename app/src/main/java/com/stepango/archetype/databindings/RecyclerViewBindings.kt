package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import com.stepango.archetype.R
import com.stepango.archetype.ui.SpaceItemDecoration

@BindingAdapter(
        "useDefaults",
        "compact",
        requireAll = false)
fun useDefaults(
        view: RecyclerView,
        useDefaults: Boolean,
        compact: Boolean?
) {
    if (!useDefaults) return
    view.clipToPadding = false
    view.apply {
        itemAnimator = DefaultItemAnimator()
    }
    view.layoutManager = LinearLayoutManager(view.context)
}

@BindingAdapter("space")
fun space(view: RecyclerView, space: Float) {
    view.addItemDecoration(SpaceItemDecoration(space.toInt()))
}

@BindingAdapter("addDivider")
fun addDivider(view: RecyclerView, divider: Boolean) {
    if (!divider) return
    val decoration = DividerItemDecoration(view.context, OrientationHelper.VERTICAL)
    decoration.setDrawable(view.context.getDrawable(R.drawable.bg_divider))
    view.addItemDecoration(decoration)
}
