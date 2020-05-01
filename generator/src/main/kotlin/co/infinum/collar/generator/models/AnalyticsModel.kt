package co.infinum.collar.generator.models

internal data class AnalyticsModel(
    val events: List<Event>,
    val screens: List<Screen>,
    val userProperties: List<Property>
)
