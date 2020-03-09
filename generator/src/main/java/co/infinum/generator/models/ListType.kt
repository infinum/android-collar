package co.infinum.generator.models

enum class ListType(val type: String) {
    TEXT("text"),
    NUMBER("number"),
    DECIMAL("decimal"),
    BOOLEAN("boolean"),
    UNKNOWN("unknown");

    companion object {
        fun getFromKey(key: String?): ListType {
            return when (key) {
                TEXT.type -> TEXT
                NUMBER.type -> NUMBER
                DECIMAL.type -> DECIMAL
                BOOLEAN.type -> BOOLEAN
                else -> UNKNOWN
            }
        }
    }
}