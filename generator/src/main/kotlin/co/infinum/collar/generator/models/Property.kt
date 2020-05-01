package co.infinum.collar.generator.models

internal data class Property(
    val name: String,
    val description: String,
    val values: List<String>?
)
