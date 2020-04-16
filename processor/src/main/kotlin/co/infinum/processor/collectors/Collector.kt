package co.infinum.processor.collectors

import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

interface Collector<Holder> {

    fun collect(): Set<Holder> = throw UnsupportedOperationException()

    fun enabled(element: Element): Boolean = throw UnsupportedOperationException()

    fun name(element: TypeElement): String = throw UnsupportedOperationException()
}
