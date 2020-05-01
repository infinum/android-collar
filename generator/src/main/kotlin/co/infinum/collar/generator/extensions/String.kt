package co.infinum.collar.generator.extensions

internal fun String.toCamelCase(): String = split(" ").map { it.capitalize() }.joinToString("")

internal fun String.hasDigit(): Boolean = this.any { it.isDigit() }
