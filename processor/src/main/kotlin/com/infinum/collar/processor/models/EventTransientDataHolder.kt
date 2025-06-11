package com.infinum.collar.processor.models

internal data class EventTransientDataHolder(
    val enabled: Boolean,
    val isSupported: Boolean,
    val resolvedName: String,
    val variableName: String,
) : Holder
