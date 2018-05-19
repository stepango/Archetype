package com.stepango.archetype.action

import android.app.Activity
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.stepango.archetype.R
import com.stepango.archetype.activity.asBaseActivity
import com.stepango.archetype.player.di.Injector
import io.reactivex.Completable

import kotlin.reflect.KClass

interface IntentMaker {
    fun make(ctx: Context, cls: KClass<out Context>) = Intent(ctx, cls.java)
    fun make(action: String) = Intent(action)
}

class IntentMakerImpl : IntentMaker

inline fun <reified T : Context> IntentMaker.intent(
        context: Context, args: Args = argsOf()
): Intent {
    val cls = T::class
    return intent(cls, context, args)
}

fun <T : Context> IntentMaker.intent(cls: KClass<T>, context: Context, args: Args)
        = make(context, cls).apply { args.let { putExtras(it) } }

inline fun <reified T : Activity> IntentMaker.startIntent(
        context: Context, args: Args = argsOf(), requestCode: Int? = null, options: Bundle? = Bundle.EMPTY
) = intent<T>(context, args).start(context, requestCode, options)

inline fun <reified T : Service> IntentMaker.startService(
        context: Context, args: Args = argsOf(), options: Bundle? = Bundle.EMPTY
) = intent<T>(context, args).startService(context, options)

fun <T : Activity> IntentMaker.startIntent(
        cls: KClass<T>, context: Context, map: Args = argsOf(), requestCode: Int? = null, options: Bundle? = Bundle.EMPTY
) = intent(cls, context, map).start(context, requestCode, options)

fun IntentMaker.startBroadcast(
        context: Context, action: String
)  = make(action).sendBroadcast(context)

// Here we are using nullable resourceType because methods like putExtra() in tests returns null
fun Intent.start(
        ctx: Context,
        requestCode: Int? = null,
        options: Bundle? = Bundle.EMPTY
): Completable = Completable.fromCallable {
    try {
        if (requestCode == null) {
            ctx.startActivity(this, options)
        } else {
            if (action.isNullOrEmpty()) {
                ctx.asBaseActivity()!!.startActivityForResultOverrode(this, requestCode, options)
            } else {
                val chooser = Intent.createChooser(this, ctx.getString(R.string.choose_action))
                ctx.asBaseActivity()!!.startActivityForResultOverrode(chooser, requestCode, options)
            }
        }
    } catch (e: Exception) {
        if (e is ActivityNotFoundException) {
            Injector().toaster().showError(e, R.string.activity_not_found_error_text)
        } else {
            throw e
        }
    }
}

fun Intent.sendBroadcast(
        ctx: Context
): Completable = Completable.fromCallable {
    ctx.sendBroadcast(this)
}

fun Intent.startService(
        ctx: Context,
        options: Bundle? = Bundle.EMPTY
): Completable = Completable.fromCallable {
    this.putExtras(options)
    ctx.startService(this)
}