package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.type.TypeMirror

internal data class EventHolder(
    val enabled: Boolean,
    val type: TypeMirror,
    val className: ClassName,
    val eventName: String,
    val eventParameters: Set<EventParameterHolder>
) : Holder
