package co.infinum.collar.generator.models

internal data class Event(
    val name: String,
    val description: String,
    val parameters: List<Parameter>
)
