package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName

data class EventHolder(
    val enabled: Boolean,
    val className: ClassName,
    val eventName: String
) : Holder