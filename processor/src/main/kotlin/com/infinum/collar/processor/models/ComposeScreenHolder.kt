package com.infinum.collar.processor.models

internal data class ComposeScreenHolder(
    val enabled: Boolean,
    val screenName: String,
    val composableName: String
) : Holder
