package com.infinum.collar.generator.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Property(

    @SerialName("description")
    val description: String? = null,

    @SerialName("name")
    val name: String,

    @SerialName("values")
    val values: List<String>? = null
)
