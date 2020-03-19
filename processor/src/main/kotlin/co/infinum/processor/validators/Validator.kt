package co.infinum.processor.validators

import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

interface Validator {

    fun verify(element: Element): TypeElement? = throw UnsupportedOperationException()

    fun supported(): List<TypeElement> = throw UnsupportedOperationException()

    fun validate(elements: MutableSet<out Element>): List<Element> = throw UnsupportedOperationException()
}