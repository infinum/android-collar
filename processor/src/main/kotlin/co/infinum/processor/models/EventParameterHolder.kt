package co.infinum.processor.models

data class EventParameterHolder(
    val enabled: Boolean,
    val method: String,
    val resolvedName: String,
    val variableName: String
) : Holder
