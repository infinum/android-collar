package co.infinum.genlib.extensions

fun String.toCamelCase(): String = split(" ").map { it.capitalize() }.joinToString("")
