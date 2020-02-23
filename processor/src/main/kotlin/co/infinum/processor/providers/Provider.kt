package co.infinum.processor.providers

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

interface Provider {

    fun collectScreenNames(roundEnvironment: RoundEnvironment): MutableSet<out Element>

    fun collectAnalyticsEvents(roundEnvironment: RoundEnvironment): MutableSet<out Element>

    fun collectUserProperties(roundEnvironment: RoundEnvironment): MutableSet<out Element>

    fun screenName(element: Element): String

    fun eventName(element: TypeElement): String

    fun eventParameterName(element: TypeElement, parameterName: String): String

    fun propertyName(element: TypeElement): String

    fun propertyParameterName(element: TypeElement, parameterName: String): String
}