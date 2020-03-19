package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.AnalyticsEventsCollector
import co.infinum.processor.configurations.Configuration
import co.infinum.processor.extensions.showError
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.EventHolder
import co.infinum.processor.models.EventParameterHolder
import co.infinum.processor.options.Options
import co.infinum.processor.specs.analyticsEventsSpec
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

class AnalyticsEventsSubprocessor(
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
        val collector = AnalyticsEventsCollector(roundEnvironment)
        val elements = collector.collect()
//        val validElements =
    }


    // TODO: Try to remove Pair from this
    private fun transformToEventHolderPair(): (Element) -> Pair<TypeElement, Map<EventHolder, List<EventParameterHolder>>>? = { annotatedElement ->
        typeElementValidator.verify(annotatedElement)?.let { analyticsElement ->
            analyticsElement to mapElementToEventHolder(analyticsElement)
        } ?: run {
            showWarning("$annotatedElement is not a sealed Kotlin class.")
            null
        }
    }


    // TODO: Try to remove Pair from this
    private fun processAnalyticsEvents(elementHolderPair: Pair<TypeElement, Map<EventHolder, List<EventParameterHolder>>>) =
        when (elementHolderPair.second.values.isEmpty()) {
            true -> showWarning("${elementHolderPair.first} has no valid inner class.")
            false -> analyticsEventsExtension(className = elementHolderPair.first.asClassName(), holders = elementHolderPair.second)
        }


    private fun analyticsEventsExtension(className: ClassName, holders: Map<EventHolder, List<EventParameterHolder>>) {
        generatedDir?.let { outputDir ->
            analyticsEventsSpec {
                outputDir(outputDir)
                className(className)
                holders(holders)
            }
        } ?: run {
            showError("Cannot find generated output dir.")
        }
    }


    // Get all the declared inner class as our Analytics Event
    private fun mapElementToEventHolder(analyticsElement: TypeElement): Map<EventHolder, List<EventParameterHolder>> {
        val analyticsEvents = mutableMapOf<EventHolder, List<EventParameterHolder>>()

        val enclosedElements = analyticsElement.enclosedElements

        if (enclosedElements.size > processorOptions.maxEventsCount) {
            showError("You can report up to ${processorOptions.maxEventsCount} different events per app. Current size is ${enclosedElements.size}.")
        } else {
            val supertype = analyticsElement.asType()

            for (element in enclosedElements) {

                val type = element.asType()

                if (element !is TypeElement) {
                    // Inner element is not a class
                    //showWarning( "$element is not a kotlin class.")
                    continue
                } else if (!typeUtils.directSupertypes(type).contains(supertype)) {
                    // Inner class does not extend from the enclosing sealed class
                    showWarning("$element does not extend from $analyticsElement.")
                    continue
                }

                val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata

                // Make use of KotlinPoet's ClassName to easily get the class' name.
                val eventClass = element.asClassName()
                val eventClassSimpleName = annotationProvider.eventName(element)

                if (eventClassSimpleName.length > processorOptions.maxEventNameSize) {
                    showError("Event names can be up to ${processorOptions.maxEventNameSize} characters long. $eventClassSimpleName is ${eventClassSimpleName.length} long.")
                    continue
                }
                if (eventClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                    showError("Event name may only contain alphanumeric characters and underscores (\"_\"). $eventClassSimpleName does not.")
                    continue
                }
                if (eventClassSimpleName.first().isLetter().not()) {
                    showError("Event name must start with an alphabetic character. ${eventClassSimpleName.first()} in $eventClassSimpleName is not a letter.")
                    continue
                }
                if (processorOptions.reservedPrefixes.any { eventClassSimpleName.startsWith(it) }) {
                    showError("The ${processorOptions.reservedPrefixes.joinToString { "\"$it\"" }} prefixes are reserved and cannot be used in ${eventClassSimpleName}.")
                    continue
                }

                // Extract the primary constructor and its parameters as the event's parameters.
                val proto = kotlinMetadata.data.classProto
                val nameResolver = kotlinMetadata.data.nameResolver

                if (proto.constructorCount == 0) {
                    showWarning("$element has no constructor.")
                    continue
                }

                val eventParameters = proto.constructorList.first().valueParameterList
                if (eventParameters.size > processorOptions.maxEventParametersCount) {
                    showError("You can associate up to ${processorOptions.maxEventParametersCount} unique parameters with each event. Current size is ${eventParameters.size}.")
                } else {
                    val mapKey = EventHolder(className = eventClass, resolvedName = eventClassSimpleName)
                    val mapValue = eventParameters.map { valueParameter ->
                        val parameterName = nameResolver.getString(valueParameter.name)
                        EventParameterHolder(
                            variableName = parameterName,
                            resolvedName = annotationProvider.eventParameterName(element, parameterName)
                        )
                    }
                    analyticsEvents[mapKey] = mapValue
                }
            }
        }

        return analyticsEvents
    }
}