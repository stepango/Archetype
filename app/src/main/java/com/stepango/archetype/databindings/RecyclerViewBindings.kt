package com.ninetyseconds.auckland.core.databindings

import android.databinding.BindingAdapter
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import com.ninetyseconds.auckland.R
import com.ninetyseconds.auckland.core.recycler.SpaceItemDecoration
import com.ninetyseconds.auckland.core.util.listColumnsCount

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
        val columnsCount = context.listColumnsCount(compact)
        GridLayoutManager(context, columnsCount)
                .apply {
                    spanSizeLookup = SpanSizeLookupImpl(view, columnsCount).apply { isSpanIndexCacheEnabled = true }
                    layoutManager = this
                }
    }
}

@BindingAdapter("chatView")
fun chatView(view: RecyclerView, chatView: Boolean) {
    if (!chatView) return
    view.apply {
        clipToPadding = false
        itemAnimator = DefaultItemAnimator()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
    }
}

class SpanSizeLookupImpl(val view: RecyclerView, val columnsCount: Int) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        val itemViewType = view.adapter.getItemViewType(position)
        return when (itemViewType) {
            R.layout.item_project     -> 1
            R.layout.item_opportunity -> 1
            else                      -> columnsCount
        }
    }
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
