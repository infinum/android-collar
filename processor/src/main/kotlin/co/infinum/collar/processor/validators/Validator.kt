package co.infinum.collar.processor.validators

import javax.lang.model.element.TypeElement

internal interface Validator<Holder> {

    fun supported(): List<TypeElement> = throw UnsupportedOperationException()

    fun validate(elements: Set<Holder>): Set<Holder> = throw UnsupportedOperationException()
}
