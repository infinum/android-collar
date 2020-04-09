package co.infinum.processor.collectors

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.processor.extensions.toLowerSnakeCase
import co.infinum.processor.models.AnalyticsEventsHolder
import co.infinum.processor.models.EventHolder
import co.infinum.processor.models.EventParameterHolder
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.hasAnnotations
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class AnalyticsEventsCollector(
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
                                eventParameters = enclosedClass
                                    .getAnnotation(Metadata::class.java)
                                    .toImmutableKmClass()
                                    .constructors
                                    .firstOrNull()
                                    ?.valueParameters
                                    .orEmpty()
                                    .map { valueParameter ->
                                        EventParameterHolder(
                                            enabled = parameterEnabled(valueParameter),
                                            variableName = valueParameter.name,
                                            resolvedName = parameterName(valueParameter)
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

    @KotlinPoetMetadataPreview
    private fun parameterEnabled(parameter: ImmutableKmValueParameter): Boolean =
        when (parameter.hasAnnotations) {
            true -> {
                val annotations = parameter.type?.annotations.orEmpty()
                if (annotations.isEmpty()) {
                    true
                } else {
                    annotations.find { it.className == ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME.name }
                        ?.arguments
                        ?.keys
                        ?.single { it == "enabled" }?.toBoolean() ?: true
                }
            }
            false -> true
        }

    @KotlinPoetMetadataPreview
    private fun parameterName(parameter: ImmutableKmValueParameter): String =
        when (parameter.hasAnnotations) {
            true -> {
                val annotations = parameter.type?.annotations.orEmpty()
                if (annotations.isEmpty()) {
                    parameter.name.toLowerSnakeCase()
                } else {
                    annotations.find { it.className == ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME.name }
                        ?.arguments
                        ?.keys
                        ?.single { it == "value" } ?: parameter.name.toLowerSnakeCase()
                }
            }
            false -> parameter.name.toLowerSnakeCase()
        }
}