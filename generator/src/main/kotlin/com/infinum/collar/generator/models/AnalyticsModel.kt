package com.infinum.collar.generator.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AnalyticsModel(

    @SerialName("screens")
    val screens: List<Screen>? = null,

    @SerialName("events")
    val events: List<Event>? = null,

    @SerialName("userProperties")
    val properties: List<Property>? = null
)
