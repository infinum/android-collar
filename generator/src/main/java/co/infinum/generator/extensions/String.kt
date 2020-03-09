package co.infinum.generator.extensions

fun String.toCamelCase(): String = split(" ").map { it.capitalize() }.joinToString("")

fun String.hasDigit(): Boolean {
    var hasDigits = false
    this.toCharArray().forEach {
        if(it.isDigit()) {
            hasDigits = true
            return@forEach
        }
    }
    return hasDigits
}
