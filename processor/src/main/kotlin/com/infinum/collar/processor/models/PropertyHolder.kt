package com.infinum.collar.processor.models

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.type.TypeMirror

internal data class PropertyHolder(
    val enabled: Boolean,
    val type: TypeMirror,
    val className: ClassName,
    val propertyName: String,
    val propertyParameterNames: Set<String>
) : Holder
