package co.infinum.processor.models

import javax.lang.model.element.TypeElement

data class UserPropertiesHolder(
    val rootClass: TypeElement,
    val propertyHolders: Set<PropertyHolder>
) : Holder
