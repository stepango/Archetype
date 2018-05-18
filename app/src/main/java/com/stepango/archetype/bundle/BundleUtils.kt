package com.stepango.archetype.bundle

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.stepango.archetype.logger.d
import com.stepango.archetype.logger.logger

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

class ViewModelStateStub private constructor(@Transient val ignore: Boolean = true) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = Unit

    companion object {
        val INSTANCE = ViewModelStateStub()

        @Suppress("unused")
        val CREATOR: Parcelable.Creator<ViewModelStateStub> = object : Parcelable.Creator<ViewModelStateStub> {

            override fun createFromParcel(source: Parcel) = ViewModelStateStub()

            override fun newArray(size: Int): Array<ViewModelStateStub?> = arrayOfNulls(size)
        }
    }
}