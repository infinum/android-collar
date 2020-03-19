package co.infinum.processor.collectors

import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

interface Collector {

    fun collect(): MutableSet<out Element> = throw UnsupportedOperationException()

    fun name(element: TypeElement): String = throw UnsupportedOperationException()

    fun parameterName(element: TypeElement, parameterName: String): String = throw UnsupportedOperationException()
}