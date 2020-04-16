package co.infinum.processor.models

import javax.lang.model.element.TypeElement

data class AnalyticsEventsHolder(
    val rootClass: TypeElement,
    val eventHolders: Set<EventHolder>
) : Holder
