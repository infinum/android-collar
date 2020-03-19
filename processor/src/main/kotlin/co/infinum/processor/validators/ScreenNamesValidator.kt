package co.infinum.processor.validators

import co.infinum.processor.collectors.ScreenNamesCollector
import co.infinum.processor.options.Options
import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class ScreenNamesValidator(
    private val processorOptions: Options,
    private val elementUtils: Elements,
    private val typeUtils: Types,
    private val collector: ScreenNamesCollector,
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Validator {

    companion object {
        val CLASS_COMPONENT_ACTIVITY = ClassName("androidx.core.app", "ComponentActivity")
        val CLASS_ACTIVITY = ClassName("android.app", "Activity")
        val CLASS_FRAGMENT = ClassName("android.app", "Fragment")
        val CLASS_SUPPORT_FRAGMENT = ClassName("android.support.v4.app", "Fragment")
        val CLASS_ANDROIDX_FRAGMENT = ClassName("androidx.fragment.app", "Fragment")
    }

    override fun supported() =
        listOf(
            elementUtils.getTypeElement(CLASS_ACTIVITY.canonicalName),
            elementUtils.getTypeElement(CLASS_FRAGMENT.canonicalName),
            elementUtils.getTypeElement(CLASS_SUPPORT_FRAGMENT.canonicalName),
            elementUtils.getTypeElement(CLASS_COMPONENT_ACTIVITY.canonicalName),
            elementUtils.getTypeElement(CLASS_ANDROIDX_FRAGMENT.canonicalName)
        )

    override fun validate(elements: MutableSet<out Element>): List<Element> =
        elements.filter { element ->
            if (isAllowed(element) != null) {
                val screenName = collector.name(element as TypeElement)
                if (screenName.length > processorOptions.maxNameSize()) {
                    onWarning("Screen names can be up to ${processorOptions.maxNameSize()} characters long. $screenName is ${screenName.length} long.")
                    false
                } else {
                    true
                }
            } else {
                onError("$element is not eligible as a screen.")
                false
            }
        }

    private fun isAllowed(element: Element): TypeElement? {
        return element.asType()?.let { typeMirror ->
            supported().find { typeElement ->
                typeElement?.asType()?.let {
                    typeUtils.isSubtype(typeMirror, typeElement.asType())
                } ?: false
            }
        }
    }
}