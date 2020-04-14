package co.infinum.collar.generator.extensions

fun String.toCamelCase(): String = split(" ").map { it.capitalize() }.joinToString("")

fun String.hasDigit(): Boolean = this.any { it.isDigit() }
