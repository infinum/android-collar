package com.infinum.collar.generator.extensions

import com.infinum.collar.generator.models.DataType
import java.util.Locale

internal fun String.toCamelCase(capitalize: Boolean = true): String = split(" ", "_")
    .joinToString("") {
        it.replaceFirstChar { value ->
            if (value.isLowerCase()) {
                value.titlecase(Locale.getDefault())
            } else {
                value.toString()
            }
        }
    }
    .replaceFirstChar {
        if (capitalize) it.uppercase() else it.lowercase()
    }

internal fun String.isFirstCharDigit(): Boolean = this.firstOrNull()?.isDigit() ?: false

internal fun String.toEnumValue(): String {
    var parameterValueEnumName = this.uppercase(Locale.ENGLISH).replace(" ", "_").replace(".", "_")
    if (this.isFirstCharDigit()) {
        parameterValueEnumName = "${DataType.NUMBER.name.uppercase(Locale.ENGLISH)}_$parameterValueEnumName"
    }
    return parameterValueEnumName
}
