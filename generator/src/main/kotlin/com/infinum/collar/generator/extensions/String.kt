package com.infinum.collar.generator.extensions

import com.infinum.collar.generator.models.DataType
import java.util.Locale

internal fun String.toCamelCase(): String = split(" ", "_").joinToString("") { it.capitalize(Locale.getDefault()) }

internal fun String.isFirstCharDigit(): Boolean = this.firstOrNull()?.isDigit() ?: false

internal fun String.toEnumValue(): String {
    var parameterValueEnumName = this.toUpperCase(Locale.ENGLISH).replace(" ", "_").replace(".", "_")
    if (this.isFirstCharDigit()) {
        parameterValueEnumName = "${DataType.NUMBER.name.toUpperCase(Locale.ENGLISH)}_$parameterValueEnumName"
    }
    return parameterValueEnumName
}
