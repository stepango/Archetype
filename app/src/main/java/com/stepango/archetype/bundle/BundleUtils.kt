package com.stepango.archetype.bundle

import android.os.Bundle
import android.os.Parcelable
import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger

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
    val name = value::class.java.name
    putParcelable(name, value)
    logger.d { "State:: saved $name" }
}