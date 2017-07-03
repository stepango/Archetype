package com.stepango.archetype.player.loader

import okhttp3.*
import okio.*

/**
 * Wild, 02.07.2017.
 */

interface ProgressUpdateListener {
    fun onProgressUpdate(current: Long, total: Long, done: Boolean)
}

class ProgressOkLoader(val listener: ProgressUpdateListener) : Interceptor {

    val client: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(this)
            .build()

    override fun intercept(chain: Interceptor.Chain?): Response? {
        val originalResponse = chain?.proceed(chain.request()) ?: return null

        return originalResponse.newBuilder()
                .body(ProgressResponseBody(originalResponse.body(), listener))
                .build()
    }
}

class ProgressResponseBody(
        val responseBody: ResponseBody,
        val listener: ProgressUpdateListener) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType() = responseBody.contentType()!!

    override fun source(): BufferedSource {
        if (bufferedSource == null)
            bufferedSource = Okio.buffer(source(responseBody.source()))

        return bufferedSource!!
    }

    override fun contentLength() = responseBody.contentLength()

    private fun source(source: Source): Source {
        return object: ForwardingSource(source) {
            private var total: Long = 0L

            override fun read(sink: Buffer?, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                total += if (bytesRead != -1L) bytesRead else 0
                listener.onProgressUpdate(total, contentLength(), bytesRead == -1L)
                return bytesRead
            }
        }
    }
}

