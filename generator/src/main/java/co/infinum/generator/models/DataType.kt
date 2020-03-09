package co.infinum.generator.models

enum class DataType(val type: String) {
    TEXT("text"),
    NUMBER("number"),
    DECIMAL("decimal"),
    BOOLEAN("boolean"),
    LIST("list"),
    UNKNOWN("unknown");

    companion object {
        fun getFromKey(key: String?): DataType {
            return when (key) {
                TEXT.type -> TEXT
                NUMBER.type -> NUMBER
                DECIMAL.type -> DECIMAL
                BOOLEAN.type -> BOOLEAN
                LIST.type -> LIST
                else -> UNKNOWN
            }
        }
    }
}