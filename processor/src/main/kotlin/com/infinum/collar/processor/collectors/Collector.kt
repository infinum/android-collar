package com.infinum.collar.processor.collectors

import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal interface Collector<Holder> {

    fun collect(): Set<Holder> = throw UnsupportedOperationException()

    fun enabled(element: Element): Boolean = throw UnsupportedOperationException()

    fun name(element: TypeElement): String = throw UnsupportedOperationException()

    fun parameterEnabled(element: Element): Boolean = throw UnsupportedOperationException()

    fun parameterName(element: Element): String = throw UnsupportedOperationException()

    fun transientDataEnabled(element: Element): Boolean = throw UnsupportedOperationException()

    fun transientDataName(element: Element): String = throw UnsupportedOperationException()
}
