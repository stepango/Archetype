package com.stepango.archetype.util

import android.net.Uri

/**
 * Wild, 03.07.2017.
 */

fun getFileName(url: String): String
        = Uri.parse(url).pathSegments.joinToString(separator = "") { it }