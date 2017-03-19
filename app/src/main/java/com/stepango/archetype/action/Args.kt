package com.stepango.archetype.action

import android.os.Bundle

typealias Args = Bundle

fun argsOf(): Args = Bundle()

fun argsOf(block: Args.() -> Unit) = argsOf().apply(block)

fun Args.copy(): Args = Bundle(this)
