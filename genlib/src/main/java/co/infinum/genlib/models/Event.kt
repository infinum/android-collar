package co.infinum.genlib.models

data class Event(
    val name: String,
    val description: String,
    val properties: List<Property>
)