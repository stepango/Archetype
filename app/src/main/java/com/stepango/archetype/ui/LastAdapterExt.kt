package com.stepango.archetype.ui

import android.databinding.BindingAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.AbsType
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.StableId
import com.stepango.archetype.BR

interface LastAdapterItem : StableId {
    fun getBindingType(): AbsType<*>?
}

@BindingAdapter("items", "scrollDown", requireAll = false)
fun lastAdapterItemsBinding(view: RecyclerView, list: List<LastAdapterItem>, scrollDown: Boolean? = false) {
    var lastItemWasVisible = false
    (view.layoutManager as? LinearLayoutManager)?.let {
        lastItemWasVisible = it.findLastCompletelyVisibleItemPosition() == view.adapter?.itemCount?.let { it - 1 }
    }
    lastAdapter(list)
            .type { item, _ -> (item as? LastAdapterItem)?.getBindingType() }
            .swap(view)
    scrollDown?.let { if (it && lastItemWasVisible) view.scrollToPosition(list.size - 1) }
}


internal fun lastAdapter(list: List<Any>, stableIds: Boolean = true) = LastAdapter(list, BR.item, stableIds)

fun LastAdapter.swap(view: RecyclerView, removeAndRecyclerExistingViews: Boolean = false) =
        view.swapAdapter(this, removeAndRecyclerExistingViews)
