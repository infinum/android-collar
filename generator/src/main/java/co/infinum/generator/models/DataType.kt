package co.infinum.generator.models

enum class DataType {
    TEXT,
    NUMBER,
    DECIMAL,
    BOOLEAN,
    LIST;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}