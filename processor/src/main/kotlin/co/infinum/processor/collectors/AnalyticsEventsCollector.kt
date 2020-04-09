package co.infinum.processor.collectors

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.processor.extensions.toLowerSnakeCase
import co.infinum.processor.models.EventHolder
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class AnalyticsEventsCollector(
    private val roundEnvironment: RoundEnvironment
) : Collector<EventHolder> {

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

    override fun collect(): Set<EventHolder> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_ANALYTICS_EVENTS).orEmpty()
            .map {
                EventHolder(
                    enabled = enabled(it),
                    className = ClassName("com.bla", "Bla"),
                    eventName = name(it)
                )
            }
            .toSet()

    override fun enabled(element: Element): Boolean =
        element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME)?.enabled ?: true

    override fun name(element: Element): String {
        val value = element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME)?.value.orEmpty()
        return when {
            value.isBlank() -> element.simpleName.toString().toLowerSnakeCase()
            else -> value
        }
    }

    override fun parameterName(element: TypeElement, parameterName: String): String =
        element.enclosedElements
            .first { it.kind == ElementKind.FIELD && it.simpleName.toString() == parameterName }
            .let {
                when {
                    it.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME) == null -> parameterName.toLowerSnakeCase()
                    else -> it.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME).value
                }
            }
}