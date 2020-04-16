package co.infinum.collar.generator.models

data class Property(
    val name: String,
    val description: String,
    val values: List<String>?
)
