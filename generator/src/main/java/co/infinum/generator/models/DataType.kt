package co.infinum.generator.models

enum class DataType(val type: String) {
    TEXT("text"),
    NUMBER("number"),
    DECIMAL("decimal"),
    BOOL("bool"),
    LIST("list"),
    UNKNOWN("unknown");

    companion object {
        fun getFromKey(key: String?): DataType {
            return when (key) {
                TEXT.type -> TEXT
                NUMBER.type -> NUMBER
                DECIMAL.type -> DECIMAL
                BOOL.type -> BOOL
                LIST.type -> LIST
                else -> UNKNOWN
            }
        }
    }
}