package com.stepango.archetype.ui

import android.support.annotation.StringRes

interface Toaster {
    fun showToast(msg: String)
    fun showToast(@StringRes id: Int, vararg args: Any)

    //    fun showError(msg: String)
    fun showError(@StringRes id: Int)

    //
    //    fun showError(t: Throwable, msg: String)
    fun showError(t: Throwable, @StringRes id: Int)
}