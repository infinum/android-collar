package co.infinum.processor.models

data class EventParameterHolder(
    val variableName: String,
    val resolvedName: String
) : Holder