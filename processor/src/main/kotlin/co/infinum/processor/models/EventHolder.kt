package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName

data class EventHolder(
    val className: ClassName,
    val resolvedName: String
)