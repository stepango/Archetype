package com.ninetyseconds.auckland.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import com.ninetyseconds.auckland.R
import com.ninetyseconds.auckland.core.activity.asBaseActivity
import com.ninetyseconds.auckland.di.lazyInject
import com.ninetyseconds.auckland.marketplace.Args
import com.ninetyseconds.auckland.marketplace.argsOf
import io.reactivex.Completable
import java.io.Serializable

val bundleFactory by lazyInject { bundleFactory }

fun Number.dp(ctx: Context): Int = (ctx.resources.displayMetrics.density * this.toFloat()).toInt()
fun Number.name(ctx: Context): String = ctx.resources.getResourceEntryName(this.toInt())
fun Context.listColumnsCount(compact: Boolean?) = this.resources.getInteger(if (compact == true) R.integer.list_compact_columns_amount else R.integer.list_columns_amount)

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

fun bundleOf(params: Iterable<Map.Entry<String, Any>>) = bundleFactory.newBundle().apply {
    params.forEach { put(it.key, it.value) }
}

fun Bundle.toArgs(): Args = this.let { bundle ->
    keySet().fold(argsOf(), { map, key ->
        map.apply { put(key, bundle.get(key)) }
    })
}

fun Args.toBundle() = bundleOf(entries)

fun Bundle.putAll(map: Args) = apply { map.forEach { put(it.key, it.value) } }

// codebeat:disable[ABC,CYCLO]
private fun Bundle.put(k: String, v: Any) {
    when (v) {
        is Boolean      -> putBoolean(k, v)
        is Byte         -> putByte(k, v)
        is Char         -> putChar(k, v)
        is Short        -> putShort(k, v)
        is Int          -> putInt(k, v)
        is Long         -> putLong(k, v)
        is Float        -> putFloat(k, v)
        is Double       -> putDouble(k, v)
        is String       -> putString(k, v)
        is CharSequence -> putCharSequence(k, v)
        is Parcelable   -> putParcelable(k, v)
        is Serializable -> putSerializable(k, v)
        is BooleanArray -> putBooleanArray(k, v)
        is ByteArray    -> putByteArray(k, v)
        is CharArray    -> putCharArray(k, v)
        is DoubleArray  -> putDoubleArray(k, v)
        is FloatArray   -> putFloatArray(k, v)
        is IntArray     -> putIntArray(k, v)
        is LongArray    -> putLongArray(k, v)
        is ShortArray   -> putShortArray(k, v)
        is Bundle       -> putBundle(k, v)
        is Array<*>     -> putGenericArray(k, v)
        else            -> throw IllegalArgumentException("Unsupported bundle component (${v::class.java})")
    }
}
// codebeat:enable[ABC,CYCLO]

private fun Bundle.putGenericArray(k: String, v: Array<*>) {
    @Suppress("UNCHECKED_CAST") when {
        v.isArrayOf<Parcelable>()   -> putParcelableArray(k, v as Array<out Parcelable>)
        v.isArrayOf<CharSequence>() -> putCharSequenceArray(k, v as Array<out CharSequence>)
        v.isArrayOf<String>()       -> putStringArray(k, v as Array<out String>)
        else                        -> throw IllegalArgumentException("Unsupported bundle component (${v::class.java})")
    }
}

fun setResultAndFinish(result: Args, context: Context)
        = setResultAndFinish(bundleOf(result.entries), context)

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


