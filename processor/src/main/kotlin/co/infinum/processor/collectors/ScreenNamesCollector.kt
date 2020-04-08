package co.infinum.processor.collectors

import co.infinum.collar.annotations.ScreenName
import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.shared.Constants
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class ScreenNamesCollector(
    private val roundEnvironment: RoundEnvironment,
    private val elementUtils: Elements,
    private val typeUtils: Types
) : Collector<ScreenHolder> {

    companion object {
        val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        val SUPPORTED = setOf(ANNOTATION_SCREEN_NAME.name)
    }

    private val supportedSuperClasses = Constants.SUPPORTED_SCREEN_NAME_CLASSES.map { elementUtils.getTypeElement(it.canonicalName) }

    override fun collect(): Set<ScreenHolder> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_SCREEN_NAME).orEmpty()
            .map {
                ScreenHolder(
                    enabled = enabled(it),
                    superClassName = superClassName(it),
                    className = (it as TypeElement).asClassName(),
                    screenName = name(it)
                )
            }
            .toSet()

    override fun enabled(element: Element): Boolean =
        element.getAnnotation(ANNOTATION_SCREEN_NAME)?.enabled ?: true

    override fun name(element: Element): String {
        val value = element.getAnnotation(ANNOTATION_SCREEN_NAME).value
        return when {
            value.isBlank() -> element.simpleName.toString()
            else -> value
        }
    }

    private fun superClassName(element: Element): ClassName? =
        supportedSuperClasses.find { superElement: TypeElement? ->
            superElement?.let { typeUtils.isSubtype(element.asType(), it.asType()) } ?: false
        }?.asClassName()
}