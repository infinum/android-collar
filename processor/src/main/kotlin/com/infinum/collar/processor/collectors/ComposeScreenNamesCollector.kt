package com.infinum.collar.processor.collectors

import com.infinum.collar.annotations.ScreenName
import com.infinum.collar.processor.extensions.asClassName
import com.infinum.collar.processor.models.ComposeScreenHolder
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class ComposeScreenNamesCollector(
    private val roundEnvironment: RoundEnvironment,
    private val elementUtils: Elements,
    private val typeUtils: Types
) : Collector<ComposeScreenHolder> {

    companion object {
        val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        val ANNOTATION_COMPOSABLAE = "androidx.compose.runtime.Composable"
    }

    override fun collect(): Set<ComposeScreenHolder> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_SCREEN_NAME).orEmpty()
            .filterIsInstance<TypeElement>()
            .filter {
                hasComposableAnnotation(it)
            }
            .map {
                ComposeScreenHolder(
                    enabled = enabled(it),
                    screenName = name(it),
                    composableName = composableName(it),
                    packageName = packageName(it)
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

    private fun packageName(element: TypeElement) = element.asClassName().packageName

    private fun composableName(element: TypeElement): String = element.asClassName().simpleName

    private fun hasComposableAnnotation(element: Element): Boolean {
        return element.annotationMirrors.any {
            val annotationElement = it.annotationType as? TypeElement ?: return@any false
            annotationElement.qualifiedName.toString() == ANNOTATION_COMPOSABLAE
        }
    }
}
