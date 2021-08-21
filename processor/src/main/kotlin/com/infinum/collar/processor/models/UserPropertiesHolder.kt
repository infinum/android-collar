package com.infinum.collar.processor.models

import javax.lang.model.element.TypeElement

internal data class UserPropertiesHolder(
    val rootClass: TypeElement,
    val propertyHolders: Set<PropertyHolder>
) : Holder
