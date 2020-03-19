package co.infinum.processor.collectors

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import co.infinum.processor.extensions.toLowerSnakeCase
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class UserPropertiesCollector(
    private val roundEnvironment: RoundEnvironment
) : Collector {

    companion object {
        val ANNOTATION_USER_PROPERTIES = UserProperties::class.java
        val ANNOTATION_PROPERTY_NAME = PropertyName::class.java
        val SUPPORTED = setOf(ANNOTATION_USER_PROPERTIES.name, ANNOTATION_PROPERTY_NAME.name)
    }

    override fun collect(): MutableSet<out Element> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_USER_PROPERTIES) ?: mutableSetOf()

    override fun name(element: TypeElement): String =
        element.getAnnotation(ANNOTATION_PROPERTY_NAME)?.value ?: run { element.asClassName().simpleName.toLowerSnakeCase() }
}