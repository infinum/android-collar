package co.infinum.processor.collectors

import co.infinum.processor.models.Holder
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

interface Collector<Holder> {

    fun collect(): Set<Holder> = throw UnsupportedOperationException()

    fun enabled(element: Element): Boolean = throw UnsupportedOperationException()

    fun name(element: Element): String = throw UnsupportedOperationException()

    fun parameterName(element: TypeElement, parameterName: String): String = throw UnsupportedOperationException()
}