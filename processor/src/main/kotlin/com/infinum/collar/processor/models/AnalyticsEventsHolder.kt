package com.infinum.collar.processor.models

import javax.lang.model.element.TypeElement

internal data class AnalyticsEventsHolder(
    val rootClass: TypeElement,
    val eventHolders: Set<EventHolder>
) : Holder
