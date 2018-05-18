package com.stepango.archetype.viewmodel

import android.content.Context
import android.view.View
import io.reactivex.Single

interface LoadingProgressBuilder<T : Any> {
    val producer: () -> Single<T>
    fun onError(handler: (Throwable) -> Unit): LoadingProgressBuilder<T>
    fun onResult(handler: (T, Context) -> Unit): LoadingProgressBuilder<T>
    fun finishAfterSuccess(finish: Boolean = true): LoadingProgressBuilder<T>
    fun show()
}

interface LoadingProgressHelper {
    fun <T : Any> loadingProgress(context: Context, producer: () -> Single<T>): LoadingProgressBuilder<T>
    fun <T : Any> loadingProgress(view: View, producer: () -> Single<T>): LoadingProgressBuilder<T> =
            loadingProgress(view.context, producer)
}

class LoadingProgressHelperImpl : LoadingProgressHelper {

    override fun <T : Any> loadingProgress(context: Context, producer: () -> Single<T>): LoadingProgressBuilder<T> =
            TODO("show loading fragment")

}