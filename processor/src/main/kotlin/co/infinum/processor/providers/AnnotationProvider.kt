package co.infinum.processor.providers

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import co.infinum.processor.extensions.toLowerSnakeCase
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class AnnotationProvider : Provider {

    companion object {
        val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        val ANNOTATION_ANALYTICS_EVENTS = AnalyticsEvents::class.java
        val ANNOTATION_ANALYTICS_EVENT_NAME = EventName::class.java
        val ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME = EventParameterName::class.java
        val ANNOTATION_USER_PROPERTIES = UserProperties::class.java
        val ANNOTATION_PROPERTY_NAME = PropertyName::class.java
        val ANNOTATION_PROPERTY_PARAMETER_NAME = EventParameterName::class.java

        val SUPPORTED: MutableSet<String> =
            mutableSetOf(
                ANNOTATION_SCREEN_NAME.name,
                ANNOTATION_ANALYTICS_EVENTS.name,
                ANNOTATION_ANALYTICS_EVENT_NAME.name,
                ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME.name,
                ANNOTATION_PROPERTY_NAME.name
            )
    }

    override fun collectScreenNames(roundEnvironment: RoundEnvironment): MutableSet<out Element> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_SCREEN_NAME) ?: mutableSetOf()


    override fun collectAnalyticsEvents(roundEnvironment: RoundEnvironment): MutableSet<out Element> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_ANALYTICS_EVENTS) ?: mutableSetOf()

    override fun collectUserProperties(roundEnvironment: RoundEnvironment): MutableSet<out Element> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_USER_PROPERTIES) ?: mutableSetOf()

    override fun screenName(element: Element): String {
        val value = element.getAnnotation(ANNOTATION_SCREEN_NAME).value
        return when {
            value.isBlank() -> element.simpleName.toString()
            else -> value
        }
    }

    override fun eventName(element: TypeElement): String =
        element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME)?.value ?: run { element.asClassName().simpleName.toLowerSnakeCase() }

    override fun eventParameterName(element: TypeElement, parameterName: String): String =
        element.enclosedElements
            .first { it.kind == ElementKind.FIELD && it.simpleName.toString() == parameterName }
            .let {
                if (it.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME) == null) {
                    parameterName.toLowerSnakeCase()
                } else {
                    it.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME).value
                }
            }

    override fun propertyName(element: TypeElement): String =
        element.getAnnotation(ANNOTATION_PROPERTY_NAME)?.value ?: run { element.asClassName().simpleName.toLowerSnakeCase() }

    override fun propertyParameterName(element: TypeElement, parameterName: String): String =
        element.enclosedElements
            .first { it.kind == ElementKind.FIELD && it.simpleName.toString() == parameterName }
            .let {
                if (it.getAnnotation(ANNOTATION_PROPERTY_PARAMETER_NAME) == null) {
                    parameterName.toLowerSnakeCase()
                } else {
                    it.getAnnotation(ANNOTATION_PROPERTY_PARAMETER_NAME).value
                }
            }
}