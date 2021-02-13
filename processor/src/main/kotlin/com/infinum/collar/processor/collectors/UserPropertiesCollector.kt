package com.infinum.collar.processor.collectors

import com.infinum.collar.annotations.PropertyName
import com.infinum.collar.annotations.UserProperties
import com.infinum.collar.processor.extensions.asClassName
import com.infinum.collar.processor.extensions.constructorParameterNames
import com.infinum.collar.processor.extensions.toLowerSnakeCase
import com.infinum.collar.processor.models.PropertyHolder
import com.infinum.collar.processor.models.UserPropertiesHolder
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal class UserPropertiesCollector(
    private val roundEnvironment: RoundEnvironment
) : Collector<UserPropertiesHolder> {

    companion object {
        val ANNOTATION_USER_PROPERTIES = UserProperties::class.java
        val ANNOTATION_PROPERTY_NAME = PropertyName::class.java
        val SUPPORTED = setOf(ANNOTATION_USER_PROPERTIES.name, ANNOTATION_PROPERTY_NAME.name)
    }

    @KotlinPoetMetadataPreview
    override fun collect(): Set<UserPropertiesHolder> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_USER_PROPERTIES).orEmpty()
            .filterIsInstance<TypeElement>()
            .map {
                UserPropertiesHolder(
                    rootClass = it,
                    propertyHolders = it.enclosedElements
                        .orEmpty()
                        .filterIsInstance<TypeElement>()
                        .map { enclosedClass ->
                            PropertyHolder(
                                enabled = enabled(enclosedClass),
                                type = enclosedClass.asType(),
                                className = enclosedClass.asClassName(),
                                propertyName = name(enclosedClass),
                                propertyParameterNames = enclosedClass.constructorParameterNames().toSet()
                            )
                        }.toSet()
                )
            }
            .toSet()

    override fun enabled(element: Element): Boolean =
        element.getAnnotation(ANNOTATION_PROPERTY_NAME)?.enabled ?: true

    override fun name(element: TypeElement): String {
        val value = element.getAnnotation(ANNOTATION_PROPERTY_NAME)?.value.orEmpty()
        return when {
            value.isBlank() -> element.asClassName().simpleName.toLowerSnakeCase()
            else -> value
        }
    }
}
