package co.infinum.collar.generator.models

data class Event(
    val name: String,
    val description: String,
    val properties: List<Parameter>? = null
)