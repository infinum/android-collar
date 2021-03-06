package com.infinum.collar.processor.collectors

import com.infinum.collar.annotations.ScreenName
import com.infinum.collar.processor.extensions.asClassName
import com.infinum.collar.processor.models.ScreenHolder
import com.infinum.collar.processor.shared.Constants
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class ScreenNamesCollector(
    private val roundEnvironment: RoundEnvironment,
    private val elementUtils: Elements,
    private val typeUtils: Types
) : Collector<ScreenHolder> {

    companion object {
        val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        val SUPPORTED = setOf(ANNOTATION_SCREEN_NAME.name)
    }

    private val supportedSuperClasses = Constants.SUPPORTED_SCREEN_NAME_CLASSES.map {
        elementUtils.getTypeElement(it.canonicalName)
    }

    override fun collect(): Set<ScreenHolder> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_SCREEN_NAME).orEmpty()
            .filterIsInstance<TypeElement>()
            .map {
                ScreenHolder(
                    enabled = enabled(it),
                    superClassName = superClassName(it),
                    className = it.asClassName(),
                    screenName = name(it)
                )
            }
            .toSet()

    override fun enabled(element: Element): Boolean =
        element.getAnnotation(ANNOTATION_SCREEN_NAME)?.enabled ?: true

    override fun name(element: TypeElement): String {
        val value = element.getAnnotation(ANNOTATION_SCREEN_NAME).value
        return when {
            value.isBlank() -> element.asClassName().simpleName
            else -> value
        }
    }

    private fun superClassName(element: Element): ClassName? =
        supportedSuperClasses.find { superElement: TypeElement? ->
            superElement?.let { typeUtils.isSubtype(element.asType(), it.asType()) } ?: false
        }?.asClassName()
}
