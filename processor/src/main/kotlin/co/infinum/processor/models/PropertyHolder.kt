package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName

data class PropertyHolder(
    val className: ClassName,
    val resolvedName: String
) : Holder