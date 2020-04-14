package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

data class ScreenHolder(
    val enabled: Boolean,
    val superClassName: ClassName?,
    val className: ClassName,
    val screenName: String
) : Holder