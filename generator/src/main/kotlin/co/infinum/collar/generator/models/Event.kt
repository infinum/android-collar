package co.infinum.collar.generator.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Event(

    @SerialName("description")
    val description: String? = null,

    @SerialName("name")
    val name: String,

    @SerialName("properties")
    val parameters: List<Parameter>? = null
)
