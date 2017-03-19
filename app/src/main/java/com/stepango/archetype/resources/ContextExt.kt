package com.stepango.archetype.resources

import android.content.Context

fun Number.name(ctx: Context): String = ctx.resources.getResourceEntryName(this.toInt())