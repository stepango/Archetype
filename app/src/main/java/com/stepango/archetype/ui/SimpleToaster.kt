package com.stepango.archetype.ui

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.widget.Toast
import com.stepango.archetype.logger.logError

class SimpleToaster(val ctx: Activity) : Toaster {

    override fun showError(@StringRes id: Int) = showToast(id)

    override fun showError(t: Throwable, @StringRes id: Int) {
        t.logError { "Show error to user" }
        showError(id)
    }

    override fun showToast(@StringRes id: Int, vararg args: Any) {
        Handler(Looper.getMainLooper()).post { Toast.makeText(ctx, ctx.getString(id, args), Toast.LENGTH_SHORT).show() }
    }

    override fun showToast(msg: String) {
        Handler(Looper.getMainLooper()).post { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
    }
}