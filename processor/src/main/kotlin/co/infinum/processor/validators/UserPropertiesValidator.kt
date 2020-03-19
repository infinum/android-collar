package co.infinum.processor.validators

import co.infinum.processor.collectors.UserPropertiesCollector
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.PropertyHolder
import co.infinum.processor.models.PropertyParameterHolder
import co.infinum.processor.options.Options
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

class UserPropertiesValidator(
    private val processorOptions: Options,
    private val elementUtils: Elements,
    private val typeUtils: Types,
    private val collector: UserPropertiesCollector,
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Validator {

    override fun verify(element: Element): TypeElement? =
        when (isKotlinSealedClass(element.kotlinMetadata, element)) {
            true -> element as TypeElement
            false -> null
        }

    override fun validate(elements: MutableSet<out Element>): List<Element> {
        elements.mapNotNull(transform = this::verify)
            .filter(predicate = this::validateSize)
    }

    private fun isKotlinSealedClass(kotlinMetadata: KotlinMetadata?, element: Element): Boolean =
        kotlinMetadata is KotlinClassMetadata && kotlinMetadata.data.classProto.modality == ProtoBuf.Modality.SEALED && element is TypeElement

    private fun transformToPropertyHolderPair(): (Element) -> Pair<TypeElement, Map<PropertyHolder, List<PropertyParameterHolder>>>? = { annotatedElement ->
        verify(annotatedElement)?.let { analyticsElement ->
            analyticsElement to mapElementToPropertyHolder(analyticsElement)
        } ?: run {
            onWarning("$annotatedElement is not a sealed Kotlin class.")
            null
        }
    }

    private fun validateSize(propertiesElement: TypeElement): Boolean {
        val enclosedElements = propertiesElement.enclosedElements
        return if (enclosedElements.size > processorOptions.maxCount()) {
            onError("You can report up to ${processorOptions.maxCount()} different user properties per app. Current size is ${enclosedElements.size}.")
            false
        } else {
            true
        }
    }

    private fun validateelems(propertiesElement: TypeElement, enclosedElements: MutableList<out Element>) {
        val supertype = propertiesElement.asType()

        for (element in enclosedElements) {

            val type = element.asType()

            if (element !is TypeElement) {
                // Inner element is not a class
                //showWarning( "$element is not a kotlin class.")
                continue
            } else if (!typeUtils.directSupertypes(type).contains(supertype)) {
                // Inner class does not extend from the enclosing sealed class
                onWarning("$element does not extend from $propertiesElement.")
                continue
            }

            val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata

            val propertyClassSimpleName = collector.name(element)

            if (propertyClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                onError("Property name may only contain alphanumeric characters and underscores (\"_\"). $propertyClassSimpleName does not.")
                continue
            }
            if (processorOptions.reserved().any { propertyClassSimpleName.equals(it, false) }) {
                onError("The ${processorOptions.reserved().joinToString { "\"$it\"" }} user properties are reserved and cannot be used in ${propertyClassSimpleName}.")
                continue
            }

            // Extract the primary constructor and its parameters as the property's parameters.
            val proto = kotlinMetadata.data.classProto
            if (proto.constructorCount == 0) {
                onWarning("$element has no constructor.")
                continue
            }

            val propertyParameters = proto.constructorList.first().valueParameterList
            if (propertyParameters.size > 1) {
                onError("You can associate up to 1 unique parameter with each user property. Current size is ${propertyParameters.size}.")
            }
        }
    }

    private fun mapElementToPropertyHolder(propertiesElement: TypeElement): Map<PropertyHolder, List<PropertyParameterHolder>> {
        val userProperties = mutableMapOf<PropertyHolder, List<PropertyParameterHolder>>()

        val enclosedElements = propertiesElement.enclosedElements

        if (enclosedElements.size > processorOptions.maxCount()) {
            onError("You can report up to ${processorOptions.maxCount()} different user properties per app. Current size is ${enclosedElements.size}.")
        } else {
            val supertype = propertiesElement.asType()

            for (element in enclosedElements) {

                val type = element.asType()

                if (element !is TypeElement) {
                    // Inner element is not a class
                    //showWarning( "$element is not a kotlin class.")
                    continue
                } else if (!typeUtils.directSupertypes(type).contains(supertype)) {
                    // Inner class does not extend from the enclosing sealed class
                    onWarning("$element does not extend from $propertiesElement.")
                    continue
                }

                val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata

                // Make use of KotlinPoet's ClassName to easily get the class' name.
                val propertyClass = element.asClassName()
                val propertyClassSimpleName = collector.name(element)

                if (propertyClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                    onError("Property name may only contain alphanumeric characters and underscores (\"_\"). $propertyClassSimpleName does not.")
                    continue
                }
                if (processorOptions.reserved().any { propertyClassSimpleName.equals(it, false) }) {
                    onError("The ${processorOptions.reserved().joinToString { "\"$it\"" }} user properties are reserved and cannot be used in ${propertyClassSimpleName}.")
                    continue
                }

                // Extract the primary constructor and its parameters as the property's parameters.
                val proto = kotlinMetadata.data.classProto
                val nameResolver = kotlinMetadata.data.nameResolver

                if (proto.constructorCount == 0) {
                    onWarning("$element has no constructor.")
                    continue
                }

                val propertyParameters = proto.constructorList.first().valueParameterList
                if (propertyParameters.size > 1) {
                    onError("You can associate up to 1 unique parameter with each user property. Current size is ${propertyParameters.size}.")
                } else {
                    val mapKey = PropertyHolder(className = propertyClass, resolvedName = propertyClassSimpleName)
                    val mapValue = propertyParameters.map { valueParameter ->
                        val parameterName = nameResolver.getString(valueParameter.name)
                        PropertyParameterHolder(
                            variableName = parameterName,
                            resolvedName = collector.parameterName(element, parameterName) // TODO: This will crash it
                        )
                    }
                    userProperties[mapKey] = mapValue
                }
            }
        }

        return userProperties
    }
}