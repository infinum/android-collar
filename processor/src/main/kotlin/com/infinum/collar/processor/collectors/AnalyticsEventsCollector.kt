package com.infinum.collar.processor.collectors

import com.infinum.collar.annotations.AnalyticsEvents
import com.infinum.collar.annotations.EventName
import com.infinum.collar.annotations.EventParameterName
import com.infinum.collar.processor.extensions.asClassName
import com.infinum.collar.processor.extensions.fieldElements
import com.infinum.collar.processor.extensions.isSupported
import com.infinum.collar.processor.extensions.toLowerSnakeCase
import com.infinum.collar.processor.models.AnalyticsEventsHolder
import com.infinum.collar.processor.models.EventHolder
import com.infinum.collar.processor.models.EventParameterHolder
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal class AnalyticsEventsCollector(
    private val roundEnvironment: RoundEnvironment
) : Collector<AnalyticsEventsHolder> {

    companion object {
        val ANNOTATION_ANALYTICS_EVENTS = AnalyticsEvents::class.java
        val ANNOTATION_ANALYTICS_EVENT_NAME = EventName::class.java
        val ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME = EventParameterName::class.java
        val SUPPORTED = setOf(
            ANNOTATION_ANALYTICS_EVENTS.name,
            ANNOTATION_ANALYTICS_EVENT_NAME.name,
            ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME.name
        )
    }

    override fun collect(): Set<AnalyticsEventsHolder> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_ANALYTICS_EVENTS).orEmpty()
            .filterIsInstance<TypeElement>()
            .map {
                AnalyticsEventsHolder(
                    rootClass = it,
                    eventHolders = it.enclosedElements
                        .orEmpty()
                        .filterIsInstance<TypeElement>()
                        .map { enclosedClass ->
                            EventHolder(
                                enabled = enabled(enclosedClass),
                                type = enclosedClass.asType(),
                                className = enclosedClass.asClassName(),
                                eventName = name(enclosedClass),
                                eventParameters = enclosedClass.fieldElements()
                                    .map { fieldParameter ->
                                        EventParameterHolder(
                                            enabled = parameterEnabled(fieldParameter),
                                            isSupported = fieldParameter.isSupported(),
                                            resolvedName = parameterName(fieldParameter),
                                            variableName = fieldParameter.simpleName.toString()
                                        )
                                    }
                                    .toSet()
                            )
                        }.toSet()
                )
            }
            .toSet()

    override fun enabled(element: Element): Boolean =
        element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME)?.enabled ?: true

    override fun name(element: TypeElement): String {
        val value = element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME)?.value.orEmpty()
        return when {
            value.isBlank() -> element.asClassName().simpleName.toLowerSnakeCase()
            else -> value
        }
    }

    override fun parameterEnabled(element: Element) =
        element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME)?.enabled ?: true

    @Suppress("SimpleRedundantLet")
    override fun parameterName(element: Element): String =
        element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME)?.let { it.value }
            ?: run { element.simpleName.toString().toLowerSnakeCase() }
}
