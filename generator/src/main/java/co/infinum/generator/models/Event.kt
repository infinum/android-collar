package co.infinum.generator.models

data class Event(
    val name: String,
    val description: String,
    val properties: List<Property>
)