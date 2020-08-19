package co.infinum.collar.generator.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Screen(

    @SerialName("description")
    val description: String? = null,

    @SerialName("name")
    val name: String
)
