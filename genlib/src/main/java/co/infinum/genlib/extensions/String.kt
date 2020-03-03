package co.infinum.genlib.extensions

fun String.toCamelCase(): String = split(" ").map { it.capitalize() }.joinToString("")

fun String.hasDigits(): Boolean {
    var hasDigits = false
    this.toCharArray().forEach {
        if(it.isDigit()) {
            hasDigits = true
            return@forEach
        }
    }
    return hasDigits
}
