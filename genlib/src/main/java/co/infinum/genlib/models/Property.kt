package co.infinum.genlib.models

data class Property(
    val name: String,
    val description: String,
    val type: String,
    val values: List<String>
)