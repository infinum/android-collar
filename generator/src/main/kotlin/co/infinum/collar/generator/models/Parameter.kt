package co.infinum.collar.generator.models

internal data class Parameter(
    val name: String,
    val description: String,
    val type: String,
    val values: List<String>?
)
