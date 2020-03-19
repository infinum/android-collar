package co.infinum.processor.collectors

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.processor.extensions.toLowerSnakeCase
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class AnalyticsEventsCollector(
    private val roundEnvironment: RoundEnvironment
) : Collector {

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

    override fun collect(): MutableSet<out Element> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_ANALYTICS_EVENTS) ?: mutableSetOf()

    override fun name(element: TypeElement): String =
        element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME)?.value ?: run { element.asClassName().simpleName.toLowerSnakeCase() }

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