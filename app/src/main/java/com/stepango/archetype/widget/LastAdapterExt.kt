package com.ninetyseconds.auckland.core.recycler

import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.stepango.archetype.BR

fun LastAdapter.Companion.with(list: List<Any>, stableIds: Boolean = true)
        = with(list, BR.item, stableIds)

fun LastAdapter.swap(view: RecyclerView, removeAndRecyclerExistingViews: Boolean = false)
        = view.swapAdapter(this, removeAndRecyclerExistingViews)
