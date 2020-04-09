package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element

data class UserPropertiesHolder(
    val rootClass: Element,
    val rootClassName: ClassName,
    val propertyHolders: Set<PropertyHolder>
) : Holder