package com.ninetyseconds.auckland.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import com.stepango.archetype.action.Args
import com.stepango.archetype.activity.asBaseActivity
import io.reactivex.Completable

fun Number.dp(ctx: Context): Int = (ctx.resources.displayMetrics.density * this.toFloat()).toInt()
fun Number.name(ctx: Context): String = ctx.resources.getResourceEntryName(this.toInt())

fun Activity.screenWidth(): Int {
    val metrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

@Throws(IllegalArgumentException::class)
fun Args.hasKeysGuarg(vararg params: String): Unit {
    val map = params.map { it to get(it) }
    if (!map.all { it.second != null }) {
        throw IllegalArgumentException("${map.filter { it.second == null }}")
    }
}

fun setResultAndFinish(bundle: Bundle, context: Context): Completable = Completable.fromCallable {
    context.asBaseActivity()?.run {
        val data = Intent().putExtras(bundle)
        if (parent == null) {
            setResult(Activity.RESULT_OK, data)
        } else {
            parent.setResult(Activity.RESULT_OK, data)
        }
        finish()
    }
}


