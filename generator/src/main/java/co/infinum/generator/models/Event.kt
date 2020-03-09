package co.infinum.generator.models

data class Event(
    val name: String,
    val description: String,
    val parameters: List<Property>
)