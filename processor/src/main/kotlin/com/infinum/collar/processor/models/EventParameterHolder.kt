package com.infinum.collar.processor.models

internal data class EventParameterHolder(
    val enabled: Boolean,
    val isSupported: Boolean,
    val resolvedName: String,
    val variableName: String
) : Holder
