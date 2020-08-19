package co.infinum.collar.generator.models

import java.util.Locale

internal enum class DataType {
    TEXT,
    NUMBER,
    DECIMAL,
    BOOLEAN;

    override fun toString(): String =
        super.toString().toLowerCase(Locale.getDefault())
}
