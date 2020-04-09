package co.infinum.processor.collectors

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import co.infinum.processor.extensions.toLowerSnakeCase
import co.infinum.processor.models.PropertyHolder
import co.infinum.processor.models.UserPropertiesHolder
import com.squareup.kotlinpoet.asClassName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class UserPropertiesCollector(
    private val roundEnvironment: RoundEnvironment
) : Collector<UserPropertiesHolder> {

    companion object {
        val ANNOTATION_USER_PROPERTIES = UserProperties::class.java
        val ANNOTATION_PROPERTY_NAME = PropertyName::class.java
        val SUPPORTED = setOf(ANNOTATION_USER_PROPERTIES.name, ANNOTATION_PROPERTY_NAME.name)
    }

    override fun collect(): Set<UserPropertiesHolder> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_USER_PROPERTIES).orEmpty()
            .map {
                UserPropertiesHolder(
                    rootClass = it,
                    rootClassName = (it as TypeElement).asClassName(),
                    propertyHolders = it.enclosedElements.orEmpty().filterIsInstance<TypeElement>().map { enclosedClass ->
                        PropertyHolder(
                            enabled = enabled(enclosedClass),
                            type = enclosedClass.asType(),
                            className = enclosedClass.asClassName(),
                            propertyName = name(enclosedClass),
                            propertyParameterNames = (enclosedClass.kotlinMetadata as KotlinClassMetadata)
                                .data
                                .classProto
                                .constructorList
                                .firstOrNull()
                                ?.valueParameterList
                                .orEmpty()
                                .map { valueParameter ->
                                    (enclosedClass.kotlinMetadata as KotlinClassMetadata)
                                        .data
                                        .nameResolver
                                        .getString(valueParameter.name)
                                }
                                .toSet()
                        )
                    }.toSet()
                )
            }
            .toSet()

    override fun enabled(element: Element): Boolean =
        element.getAnnotation(ANNOTATION_PROPERTY_NAME)?.enabled ?: true

    override fun name(element: Element): String {
        val value = element.getAnnotation(ANNOTATION_PROPERTY_NAME)?.value.orEmpty()
        return when {
            value.isBlank() -> element.simpleName.toString().toLowerSnakeCase()
            else -> value
        }
    }
}