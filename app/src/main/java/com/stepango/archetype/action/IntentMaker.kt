package com.stepango.archetype.action

import android.app.Activity
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
    fun make(ctx: Context, cls: KClass<out Activity>) = Intent(ctx, cls.java)
}

class IntentMakerImpl : IntentMaker

interface IntentMakerHolder {
    val intentMaker: IntentMaker
}

inline fun <reified T : Activity> IntentMakerHolder.intent(
        context: Context, args: Args = argsOf()
): Intent {
    val cls = T::class
    return intent(cls, context, args)
}

fun <T : Activity> IntentMakerHolder.intent(cls: KClass<T>, context: Context, args: Args)
        = intentMaker.make(context, cls).apply { args.let { putExtras(it) } }

inline fun <reified T : Activity> IntentMakerHolder.startIntent(
        context: Context, args: Args = argsOf(), requestCode: Int? = null, options: Bundle? = Bundle.EMPTY
) = intent<T>(context, args).start(context, requestCode, options)

fun <T : Activity> IntentMakerHolder.startIntent(
        cls: KClass<T>, context: Context, map: Args = argsOf(), requestCode: Int? = null, options: Bundle? = Bundle.EMPTY
) = intent(cls, context, map).start(context, requestCode, options)

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