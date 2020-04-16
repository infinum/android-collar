package co.infinum.processor.validators

import javax.lang.model.element.TypeElement

interface Validator<Holder> {

    fun supported(): List<TypeElement> = throw UnsupportedOperationException()

    fun validate(elements: Set<Holder>): Set<Holder> = throw UnsupportedOperationException()
}
