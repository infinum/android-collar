package co.infinum.collar.generator.extensions

import java.util.Locale

internal fun String.toCamelCase(): String = split(" ", "_").joinToString("") { it.capitalize(Locale.getDefault()) }

internal fun String.hasDigit(): Boolean = this.any { it.isDigit() }

internal fun String.toEnumValue(): String {
    var parameterValueEnumName = this.toUpperCase(Locale.ENGLISH).replace(" ", "_").replace(".", "_")
    if (this.hasDigit()) {
        parameterValueEnumName = "NUMBER_$parameterValueEnumName"
    }
    return parameterValueEnumName
}
