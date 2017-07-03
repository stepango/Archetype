package com.stepango.archetype.util

import android.net.Uri

/**
 * Wild, 03.07.2017.
 */

fun getFileName(url: String): String {
    val uri = Uri.parse(url)
    var name = ""
    for (path in uri.pathSegments)
        name += path
    return name
}