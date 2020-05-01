package co.infinum.processor.collectors

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.processor.extensions.fieldElements
import co.infinum.processor.extensions.resolveMethod
import co.infinum.processor.extensions.toLowerSnakeCase
import co.infinum.processor.models.AnalyticsEventsHolder
import co.infinum.processor.models.EventHolder
import co.infinum.processor.models.EventParameterHolder
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
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

    @KotlinPoetMetadataPreview
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
                                            method = fieldParameter.resolveMethod(),
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
