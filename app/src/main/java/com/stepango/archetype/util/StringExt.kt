package com.stepango.archetype.util

import java.util.StringTokenizer

fun String.linesCount() = StringTokenizer(this, "\r\n").countTokens()
fun String.firstLine() = this.split("\r\n|\r|\n").first()

