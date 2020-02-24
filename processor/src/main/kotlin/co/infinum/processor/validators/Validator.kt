package co.infinum.processor.validators

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

interface Validator {

    fun verify(element: Element): TypeElement?

    fun isAllowed(element: Element?): TypeElement?

    fun resolve(element: TypeElement): ClassName?
}