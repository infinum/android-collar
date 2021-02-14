package com.infinum.collar.generator.models

import com.squareup.kotlinpoet.ClassName
import java.util.Locale

internal enum class DataType(val className: ClassName) {
    TEXT(ClassName("kotlin", "String")),
    NUMBER(ClassName("kotlin", "Long")),
    DECIMAL(ClassName("kotlin", "Double")),
    BOOLEAN(ClassName("kotlin", "Boolean"));

    companion object {

        operator fun invoke(name: String) = values().firstOrNull { it.toString().toLowerCase(Locale.ENGLISH) == name }
    }
}
