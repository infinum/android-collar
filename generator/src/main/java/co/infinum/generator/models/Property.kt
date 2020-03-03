package co.infinum.generator.models

data class Property(
    val name: String,
    val description: String,
    val type: String,
    val listType: String?,
    val values: List<String>?
)