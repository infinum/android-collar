package co.infinum.genlib.models

enum class ListType(val type: String) {
    TEXT("text"),
    NUMBER("number"),
    DECIMAL("decimal"),
    BOOL("bool"),
    UNKNOWN("unknown");

    companion object {
        fun getFromKey(key: String?): ListType {
            return when (key) {
                TEXT.type -> TEXT
                NUMBER.type -> NUMBER
                DECIMAL.type -> DECIMAL
                BOOL.type -> BOOL
                else -> UNKNOWN
            }
        }
    }
}