package com.ninetyseconds.auckland.core.bundle

import android.os.Bundle
import android.os.Parcelable
import com.ninetyseconds.auckland.core.hack.AutoSerializable
import com.ninetyseconds.auckland.core.log.d
import com.ninetyseconds.auckland.core.log.logger

interface BundleFactory {
    fun newBundle(): Bundle
    fun emptyBundle(): Bundle
}

class BundleFactoryImpl : BundleFactory {
    override fun newBundle(): Bundle = Bundle()
    override fun emptyBundle(): Bundle = Bundle.EMPTY
}

inline fun <reified T : Parcelable> Bundle?.extract(defaultValueProducer: () -> T): T {
    this ?: return defaultValueProducer()
    val key = T::class.java.name
    if (containsKey(key)) {
        logger.d { "State:: restored $key" }
        return getParcelable(key)
    } else {
        return defaultValueProducer()
    }
}

fun <T : Parcelable> Bundle.putState(value: T) {
    if (value is ViewModelStateStub) return
    val name = value::class.java.name
    putParcelable(name, value)
    logger.d { "State:: saved $name" }
}

data class ViewModelStateStub(@Transient val ignore: Boolean = true) : AutoSerializable {
    companion object {
        val INSTANCE = ViewModelStateStub()
    }
}