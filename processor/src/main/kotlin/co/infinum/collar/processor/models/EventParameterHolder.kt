package co.infinum.collar.processor.models

internal data class EventParameterHolder(
    val enabled: Boolean,
    val method: String,
    val resolvedName: String,
    val variableName: String
) : Holder
