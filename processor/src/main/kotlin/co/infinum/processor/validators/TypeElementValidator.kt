package co.infinum.processor.validators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class TypeElementValidator(
    elements: Elements,
    private val typeUtils: Types
) : Validator {

    companion object {
        val CLASS_COMPONENT_ACTIVITY = ClassName("androidx.core.app", "ComponentActivity")
        val CLASS_ACTIVITY = ClassName("android.app", "Activity")
        val CLASS_FRAGMENT = ClassName("android.app", "Fragment")
        val CLASS_SUPPORT_FRAGMENT = ClassName("android.support.v4.app", "Fragment")
        val CLASS_ANDROIDX_FRAGMENT = ClassName("androidx.fragment.app", "Fragment")
    }

    private val allowedTypeElements: List<TypeElement>

    init {
        with(elements) {
            allowedTypeElements = listOf(
                getTypeElement(CLASS_ACTIVITY.canonicalName),
                getTypeElement(CLASS_FRAGMENT.canonicalName),
                getTypeElement(CLASS_SUPPORT_FRAGMENT.canonicalName),
                getTypeElement(CLASS_COMPONENT_ACTIVITY.canonicalName),
                getTypeElement(CLASS_ANDROIDX_FRAGMENT.canonicalName)
            )
        }
    }

    override fun verify(element: Element): TypeElement? =
        when (isKotlinSealedClass(element.kotlinMetadata, element)) {
            true -> element as TypeElement
            false -> null
        }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun isAllowed(element: Element?): TypeElement? =
        element?.asType()?.let { typeMirror ->
            allowedTypeElements.find { typeElement ->
                typeElement?.asType()?.let {
                    typeUtils.isSubtype(typeMirror, typeElement.asType())
                } ?: false
            }
        }

    override fun resolve(element: TypeElement): ClassName? = allowedTypeElements.find { element == it }?.asClassName()

    private fun isKotlinSealedClass(kotlinMetadata: KotlinMetadata?, element: Element): Boolean =
        kotlinMetadata is KotlinClassMetadata && kotlinMetadata.data.classProto.modality == ProtoBuf.Modality.SEALED && element is TypeElement
}