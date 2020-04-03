package co.infinum.collar.generator.models

enum class ListType {
    TEXT,
    NUMBER,
    DECIMAL,
    BOOLEAN,
    UNKNOWN;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}