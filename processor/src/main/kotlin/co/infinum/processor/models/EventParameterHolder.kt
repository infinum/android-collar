package co.infinum.processor.models

data class EventParameterHolder(
    val enabled: Boolean,
    val variableName: String,
    val resolvedName: String
) : Holder