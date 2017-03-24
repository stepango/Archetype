package com.ninetyseconds.auckland.core.databindings

import android.databinding.BindingAdapter
import android.os.Parcelable
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.ninetyseconds.auckland.core.animation.collapseOnUpdate
import com.ninetyseconds.auckland.core.animation.expandOnUpdate
import com.ninetyseconds.auckland.core.log.logger
import com.ninetyseconds.auckland.core.util.dp
import com.ninetyseconds.auckland.core.util.setResultAndFinish
import com.ninetyseconds.auckland.core.widget.stopAnimation
import com.ninetyseconds.auckland.core.widget.visible
import com.ninetyseconds.auckland.di.Injector
import com.ninetyseconds.auckland.marketplace.argsOf
import io.reactivex.rxkotlin.subscribeBy

@BindingAdapter("visible", "animate", "resize", requireAll = false)
fun visible(v: View, visible: Boolean, animate: Boolean?, resize: Boolean?) {
    synchronized(v) {
        if (v.visible == visible) return
        if (animate != false) {
            if (visible) showView(resize, v) else hideView(resize, v)
        } else {
            v.visible = visible
        }
    }
}

private fun hideView(resize: Boolean?, v: View) {
    v.apply {
        stopAnimation()
        alpha = 1f
        val height = layoutParams.height
        animate().alpha(0f)
                .apply { if (resize == true) collapseOnUpdate(height, v) }
                .withEndAction { visible = false; layoutParams.height = height }
                .withLayer()
                .start()
    }
}

private fun showView(resize: Boolean?, v: View) {
    v.apply {
        stopAnimation()
        alpha = 0f
        val height = layoutParams.height
        if (resize == true) layoutParams.height = 0
        animate().alpha(1f)
                .apply { if (resize == true) expandOnUpdate(height, v) }
                .withStartAction { visible = true }
                .withLayer()
                .start()
    }
}

@BindingAdapter("requestFocus")
fun requestFocus(v: View, focus: Boolean) {
    if (focus) v.post { v.requestFocus() }
}

@BindingAdapter("showBottomSheet")
fun showBottomSheet(v: View, show: Boolean) {
    BottomSheetBehavior.from(v).apply {
        if (show) {
            state = BottomSheetBehavior.STATE_EXPANDED
            peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        } else {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = 56.dp(v.context)
        }
    }
}

@BindingAdapter("onClickActivityResult", "resultKey")
fun finishActivityWithResult(v: View, parcelable: ParcelableFunction, key: String) {
    v.setOnClickListener {
        //TODO: move to action
        setResultAndFinish(argsOf(key to parcelable()), v.context)
                .subscribeBy(
                        onError = { Injector.logger.e(it, "Error finishing activity") }
                )
    }
}

interface ParcelableFunction : () -> Parcelable
