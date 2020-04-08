package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName

data class PropertyHolder(
    val enabled: Boolean,
    val className: ClassName,
    val propertyName: String
) : Holder