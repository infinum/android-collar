package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.UserPropertiesCollector
import co.infinum.processor.configurations.Configuration
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.PropertyHolder
import co.infinum.processor.models.PropertyParameterHolder
import co.infinum.processor.options.Options
import co.infinum.processor.specs.userPropertiesSpec
import co.infinum.processor.validators.UserPropertiesValidator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class UserPropertiesSubprocessor(
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Subprocessor {

    private var generatedDir: File? = null
    private lateinit var processorOptions: Options
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    override fun init(configuration: Configuration) =
        with(configuration) {
            generatedDir = outputDir()
            processorOptions = options()
            elementUtils = elementUtils()
            typeUtils = typeUtils()
        }

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = UserPropertiesCollector(roundEnvironment)
        val elements = collector.collect()
        val validElements = UserPropertiesValidator(elements).validate(elements)
    }

//    private fun transformToPropertyHolderPair(): (Element) -> Pair<TypeElement, Map<PropertyHolder, List<PropertyParameterHolder>>>? = { annotatedElement ->
//        typeElementValidator.verify(annotatedElement)?.let { analyticsElement ->
//            analyticsElement to mapElementToPropertyHolder(analyticsElement)
//        } ?: run {
//            showWarning("$annotatedElement is not a sealed Kotlin class.")
//            null
//        }
//    }


    // TODO: Try to remove Pair from this
    private fun processUserProperties(elementHolderPair: Pair<TypeElement, Map<PropertyHolder, List<PropertyParameterHolder>>>) =
        when (elementHolderPair.second.values.isEmpty()) {
            true -> onWarning("${elementHolderPair.first} has no valid inner class.")
            false -> userPropertiesExtension(className = elementHolderPair.first.asClassName(), holders = elementHolderPair.second)
        }


    private fun userPropertiesExtension(className: ClassName, holders: Map<PropertyHolder, List<PropertyParameterHolder>>) {
        generatedDir?.let { outputDir ->
            userPropertiesSpec {
                outputDir(outputDir)
                className(className)
                holders(holders)
            }
        } ?: run {
            onError("Cannot find generated output dir.")
        }
    }


//    private fun mapElementToPropertyHolder(propertiesElement: TypeElement): Map<PropertyHolder, List<PropertyParameterHolder>> {
//        val userProperties = mutableMapOf<PropertyHolder, List<PropertyParameterHolder>>()
//
//        val enclosedElements = propertiesElement.enclosedElements
//
//        if (enclosedElements.size > processorOptions.maxCount()) {
//            onError("You can report up to ${processorOptions.maxCount()} different user properties per app. Current size is ${enclosedElements.size}.")
//        } else {
//            val supertype = propertiesElement.asType()
//
//            for (element in enclosedElements) {
//
//                val type = element.asType()
//
//                if (element !is TypeElement) {
//                    // Inner element is not a class
//                    //showWarning( "$element is not a kotlin class.")
//                    continue
//                } else if (!typeUtils.directSupertypes(type).contains(supertype)) {
//                    // Inner class does not extend from the enclosing sealed class
//                    onWarning("$element does not extend from $propertiesElement.")
//                    continue
//                }
//
//                val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata
//
//                // Make use of KotlinPoet's ClassName to easily get the class' name.
//                val propertyClass = element.asClassName()
//                val propertyClassSimpleName = collector.name(element)
//
//                if (propertyClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
//                    onError("Property name may only contain alphanumeric characters and underscores (\"_\"). $propertyClassSimpleName does not.")
//                    continue
//                }
//                if (processorOptions.reserved().any { propertyClassSimpleName.equals(it, false) }) {
//                    onError("The ${processorOptions.reserved().joinToString { "\"$it\"" }} user properties are reserved and cannot be used in ${propertyClassSimpleName}.")
//                    continue
//                }
//
//                // Extract the primary constructor and its parameters as the property's parameters.
//                val proto = kotlinMetadata.data.classProto
//                val nameResolver = kotlinMetadata.data.nameResolver
//
//                if (proto.constructorCount == 0) {
//                    onWarning("$element has no constructor.")
//                    continue
//                }
//
//                val propertyParameters = proto.constructorList.first().valueParameterList
//                if (propertyParameters.size > 1) {
//                    onError("You can associate up to 1 unique parameter with each user property. Current size is ${propertyParameters.size}.")
//                } else {
//                    val mapKey = PropertyHolder(className = propertyClass, resolvedName = propertyClassSimpleName)
//                    val mapValue = propertyParameters.map { valueParameter ->
//                        val parameterName = nameResolver.getString(valueParameter.name)
//                        PropertyParameterHolder(
//                            variableName = parameterName,
//                            resolvedName = collector.parameterName(element, parameterName)
//                        )
//                    }
//                    userProperties[mapKey] = mapValue
//                }
//            }
//        }
//
//        return userProperties
//    }
}