package co.infinum.processor.collectors

import co.infinum.collar.annotations.ScreenName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class ScreenNamesCollector(
    private val roundEnvironment: RoundEnvironment
) : Collector {

    companion object {
        val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        val SUPPORTED = setOf(ANNOTATION_SCREEN_NAME.name)
    }

    override fun collect(): MutableSet<out Element> =
        roundEnvironment.getElementsAnnotatedWith(ANNOTATION_SCREEN_NAME) ?: mutableSetOf()

    override fun name(element: TypeElement): String {
        val value = element.getAnnotation(ANNOTATION_SCREEN_NAME).value
        return when {
            value.isBlank() -> element.simpleName.toString()
            else -> value
        }
    }
}