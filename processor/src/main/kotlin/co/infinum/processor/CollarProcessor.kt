package co.infinum.processor

import co.infinum.processor.collectors.AnalyticsEventsCollector
import co.infinum.processor.collectors.ScreenNamesCollector
import co.infinum.processor.collectors.UserPropertiesCollector
import co.infinum.processor.configurations.AnalyticsEventsConfiguration
import co.infinum.processor.configurations.ScreenNamesConfiguration
import co.infinum.processor.configurations.UserPropertiesConfiguration
import co.infinum.processor.extensions.consume
import co.infinum.processor.extensions.showError
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.options.AnalyticsEventsOptions
import co.infinum.processor.options.ScreenNamesOptions
import co.infinum.processor.options.UserPropertiesOptions
import co.infinum.processor.subprocessors.AnalyticsEventsSubprocessor
import co.infinum.processor.subprocessors.ScreenNamesSubprocessor
import co.infinum.processor.subprocessors.UserPropertiesSubprocessor
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class CollarProcessor : KotlinAbstractProcessor(), KotlinMetadataUtils {

    private val screenNamesSubprocessor: ScreenNamesSubprocessor = ScreenNamesSubprocessor(
        onWarning = this::showWarning,
        onError = this::showError
    )
    private val analyticsEventsSubprocessor: AnalyticsEventsSubprocessor = AnalyticsEventsSubprocessor(
        onWarning = this::showWarning,
        onError = this::showError
    )
    private val userPropertiesSubprocessor: UserPropertiesSubprocessor = UserPropertiesSubprocessor(
        onWarning = this::showWarning,
        onError = this::showError
    )

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        (ScreenNamesCollector.SUPPORTED + AnalyticsEventsCollector.SUPPORTED + UserPropertiesCollector.SUPPORTED).toMutableSet()

    override fun getSupportedSourceVersion(): SourceVersion =
        SourceVersion.latestSupported()

    override fun getSupportedOptions(): Set<String> =
        ScreenNamesOptions.SUPPORTED + AnalyticsEventsOptions.SUPPORTED + UserPropertiesOptions.SUPPORTED

    override fun init(processingEnv: ProcessingEnvironment) =
        super.init(processingEnv).run {
            screenNamesSubprocessor.init(ScreenNamesConfiguration(generatedDir, processingEnv))
            analyticsEventsSubprocessor.init(AnalyticsEventsConfiguration(generatedDir, processingEnv))
            userPropertiesSubprocessor.init(UserPropertiesConfiguration(generatedDir, processingEnv))
        }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean =
        consume {
            screenNamesSubprocessor.process(roundEnv)
            analyticsEventsSubprocessor.process(roundEnv)
//            annotationProvider.collectAnalyticsEvents(roundEnv)

//                .mapNotNull(transform = transformToEventHolderPair())
//                .forEach { processAnalyticsEvents(it) }
//
            userPropertiesSubprocessor.process(roundEnv)
//            annotationProvider.collectUserProperties(roundEnv)

//                .mapNotNull(transform = transformToPropertyHolderPair())
//                .forEach { processUserProperties(it) }
//        }

//    // TODO: Try to remove Pair from this
//    private fun transformToEventHolderPair(): (Element) -> Pair<TypeElement, Map<EventHolder, List<EventParameterHolder>>>? = { annotatedElement ->
//        typeElementValidator.verify(annotatedElement)?.let { analyticsElement ->
//            analyticsElement to mapElementToEventHolder(analyticsElement)
//        } ?: run {
//            showWarning("$annotatedElement is not a sealed Kotlin class.")
//            null
//        }
//    }

//    private fun transformToPropertyHolderPair(): (Element) -> Pair<TypeElement, Map<PropertyHolder, List<PropertyParameterHolder>>>? = { annotatedElement ->
//        typeElementValidator.verify(annotatedElement)?.let { analyticsElement ->
//            analyticsElement to mapElementToPropertyHolder(analyticsElement)
//        } ?: run {
//            showWarning("$annotatedElement is not a sealed Kotlin class.")
//            null
//        }
//    }

//    // TODO: Try to remove Pair from this
//    private fun processAnalyticsEvents(elementHolderPair: Pair<TypeElement, Map<EventHolder, List<EventParameterHolder>>>) =
//        when (elementHolderPair.second.values.isEmpty()) {
//            true -> showWarning("${elementHolderPair.first} has no valid inner class.")
//            false -> analyticsEventsExtension(className = elementHolderPair.first.asClassName(), holders = elementHolderPair.second)
//        }

//    // TODO: Try to remove Pair from this
//    private fun processUserProperties(elementHolderPair: Pair<TypeElement, Map<PropertyHolder, List<PropertyParameterHolder>>>) =
//        when (elementHolderPair.second.values.isEmpty()) {
//            true -> showWarning("${elementHolderPair.first} has no valid inner class.")
//            false -> userPropertiesExtension(className = elementHolderPair.first.asClassName(), holders = elementHolderPair.second)
//        }

//    private fun analyticsEventsExtension(className: ClassName, holders: Map<EventHolder, List<EventParameterHolder>>) {
//        generatedDir?.let { outputDir ->
//            analyticsEventsSpec {
//                outputDir(outputDir)
//                className(className)
//                holders(holders)
//            }
//        } ?: run {
//            showError("Cannot find generated output dir.")
//        }
//    }

//    private fun userPropertiesExtension(className: ClassName, holders: Map<PropertyHolder, List<PropertyParameterHolder>>) {
//        generatedDir?.let { outputDir ->
//            userPropertiesSpec {
//                outputDir(outputDir)
//                className(className)
//                holders(holders)
//            }
//        } ?: run {
//            showError("Cannot find generated output dir.")
//        }
        }

//    // Get all the declared inner class as our Analytics Event
//    private fun mapElementToEventHolder(analyticsElement: TypeElement): Map<EventHolder, List<EventParameterHolder>> {
//        val analyticsEvents = mutableMapOf<EventHolder, List<EventParameterHolder>>()
//
//        val enclosedElements = analyticsElement.enclosedElements
//
//        if (enclosedElements.size > processorOptions.maxEventsCount) {
//            showError("You can report up to ${processorOptions.maxEventsCount} different events per app. Current size is ${enclosedElements.size}.")
//        } else {
//            val supertype = analyticsElement.asType()
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
//                    showWarning("$element does not extend from $analyticsElement.")
//                    continue
//                }
//
//                val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata
//
//                // Make use of KotlinPoet's ClassName to easily get the class' name.
//                val eventClass = element.asClassName()
//                val eventClassSimpleName = annotationProvider.eventName(element)
//
//                if (eventClassSimpleName.length > processorOptions.maxEventNameSize) {
//                    showError("Event names can be up to ${processorOptions.maxEventNameSize} characters long. $eventClassSimpleName is ${eventClassSimpleName.length} long.")
//                    continue
//                }
//                if (eventClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
//                    showError("Event name may only contain alphanumeric characters and underscores (\"_\"). $eventClassSimpleName does not.")
//                    continue
//                }
//                if (eventClassSimpleName.first().isLetter().not()) {
//                    showError("Event name must start with an alphabetic character. ${eventClassSimpleName.first()} in $eventClassSimpleName is not a letter.")
//                    continue
//                }
//                if (processorOptions.reservedPrefixes.any { eventClassSimpleName.startsWith(it) }) {
//                    showError("The ${processorOptions.reservedPrefixes.joinToString { "\"$it\"" }} prefixes are reserved and cannot be used in ${eventClassSimpleName}.")
//                    continue
//                }
//
//                // Extract the primary constructor and its parameters as the event's parameters.
//                val proto = kotlinMetadata.data.classProto
//                val nameResolver = kotlinMetadata.data.nameResolver
//
//                if (proto.constructorCount == 0) {
//                    showWarning("$element has no constructor.")
//                    continue
//                }
//
//                val eventParameters = proto.constructorList.first().valueParameterList
//                if (eventParameters.size > processorOptions.maxEventParametersCount) {
//                    showError("You can associate up to ${processorOptions.maxEventParametersCount} unique parameters with each event. Current size is ${eventParameters.size}.")
//                } else {
//                    val mapKey = EventHolder(className = eventClass, resolvedName = eventClassSimpleName)
//                    val mapValue = eventParameters.map { valueParameter ->
//                        val parameterName = nameResolver.getString(valueParameter.name)
//                        EventParameterHolder(
//                            variableName = parameterName,
//                            resolvedName = annotationProvider.eventParameterName(element, parameterName)
//                        )
//                    }
//                    analyticsEvents[mapKey] = mapValue
//                }
//            }
//        }
//
//        return analyticsEvents
//    }

//    private fun mapElementToPropertyHolder(propertiesElement: TypeElement): Map<PropertyHolder, List<PropertyParameterHolder>> {
//        val userProperties = mutableMapOf<PropertyHolder, List<PropertyParameterHolder>>()
//
//        val enclosedElements = propertiesElement.enclosedElements
//
//        if (enclosedElements.size > processorOptions.maxPropertiesCount) {
//            showError("You can report up to ${processorOptions.maxPropertiesCount} different user properties per app. Current size is ${enclosedElements.size}.")
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
//                    showWarning("$element does not extend from $propertiesElement.")
//                    continue
//                }
//
//                val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata
//
//                // Make use of KotlinPoet's ClassName to easily get the class' name.
//                val propertyClass = element.asClassName()
//                val propertyClassSimpleName = annotationProvider.propertyName(element)
//
//                if (propertyClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
//                    showError("Property name may only contain alphanumeric characters and underscores (\"_\"). $propertyClassSimpleName does not.")
//                    continue
//                }
//                if (processorOptions.reservedProperties.any { propertyClassSimpleName.equals(it, false) }) {
//                    showError("The ${processorOptions.reservedProperties.joinToString { "\"$it\"" }} user properties are reserved and cannot be used in ${propertyClassSimpleName}.")
//                    continue
//                }
//
//                // Extract the primary constructor and its parameters as the property's parameters.
//                val proto = kotlinMetadata.data.classProto
//                val nameResolver = kotlinMetadata.data.nameResolver
//
//                if (proto.constructorCount == 0) {
//                    showWarning("$element has no constructor.")
//                    continue
//                }
//
//                val propertyParameters = proto.constructorList.first().valueParameterList
//                if (propertyParameters.size > 1) {
//                    showError("You can associate up to 1 unique parameter with each user property. Current size is ${propertyParameters.size}.")
//                } else {
//                    val mapKey = PropertyHolder(className = propertyClass, resolvedName = propertyClassSimpleName)
//                    val mapValue = propertyParameters.map { valueParameter ->
//                        val parameterName = nameResolver.getString(valueParameter.name)
//                        PropertyParameterHolder(
//                            variableName = parameterName,
//                            resolvedName = annotationProvider.propertyParameterName(element, parameterName)
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