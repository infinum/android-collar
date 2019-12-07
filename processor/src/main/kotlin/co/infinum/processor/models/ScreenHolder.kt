package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.TypeElement

data class ScreenHolder(
    val typeElement: TypeElement,
    val className: ClassName,
    val screenName: String
) : Holder