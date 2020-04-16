package co.infinum.collar.generator.models

enum class DataType {
    TEXT,
    NUMBER,
    DECIMAL,
    BOOLEAN;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}
