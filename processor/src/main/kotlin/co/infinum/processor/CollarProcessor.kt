package co.infinum.processor

import co.infinum.processor.extensions.consume
import co.infinum.processor.extensions.showError
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.EventHolder
import co.infinum.processor.models.EventParameterHolder
import co.infinum.processor.models.PropertyHolder
import co.infinum.processor.models.PropertyParameterHolder
import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.options.ProcessorOptions
import co.infinum.processor.providers.AnnotationProvider
import co.infinum.processor.specs.analyticsEventsSpec
import co.infinum.processor.specs.userPropertiesSpec
import co.infinum.processor.specs.screenNameSpec
import co.infinum.processor.validators.TypeElementValidator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement


@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class CollarProcessor : KotlinAbstractProcessor(), KotlinMetadataUtils {

    private val annotationProvider: AnnotationProvider = AnnotationProvider()
    private lateinit var typeElementValidator: TypeElementValidator
    private lateinit var processorOptions: ProcessorOptions

    override fun getSupportedAnnotationTypes(): MutableSet<String> = AnnotationProvider.SUPPORTED

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedOptions(): Set<String> = ProcessorOptions.SUPPORTED

    override fun init(processingEnv: ProcessingEnvironment) =
        super.init(processingEnv).run {
            processorOptions = ProcessorOptions(processingEnv.options)
            typeElementValidator = TypeElementValidator(processingEnv.elementUtils, typeUtils)
        }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean =
        consume {
            annotationProvider.collectScreenNames(roundEnv)
                .filter(predicate = validateElements())
                .map(transform = transformToScreenHolder())
                .also(block = processScreenNames())

            annotationProvider.collectAnalyticsEvents(roundEnv)
                .mapNotNull(transform = transformToEventHolderPair())
                .forEach { processAnalyticsEvents(it) }

            annotationProvider.collectUserProperties(roundEnv)
                .mapNotNull(transform = transformToPropertyHolderPair())
                .forEach { processUserProperties(it) }
        }

    private fun validateElements(): (Element) -> Boolean = { element ->
        if (typeElementValidator.isAllowed(element) != null) {
            val screenName = annotationProvider.screenName(element)
            if (screenName.length > processorOptions.maxScreenNameSize) {
                showWarning("Screen names can be up to ${processorOptions.maxScreenNameSize} characters long. $screenName is ${screenName.length} long.")
                false
            } else {
                true
            }
        } else {
            showWarning("$element is not eligible as a screen.")
            false
        }
    }

    private fun transformToScreenHolder(): (Element?) -> ScreenHolder = { element ->
        ScreenHolder(
            typeElement = typeElementValidator.isAllowed(element) as TypeElement,
            className = (element as TypeElement).asClassName(),
            screenName = annotationProvider.screenName(element)
        )
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

    private fun transformToPropertyHolderPair(): (Element) -> Pair<TypeElement, Map<PropertyHolder, List<PropertyParameterHolder>>>? = { annotatedElement ->
        typeElementValidator.verify(annotatedElement)?.let { analyticsElement ->
            analyticsElement to mapElementToPropertyHolder(analyticsElement)
        } ?: run {
            showWarning("$annotatedElement is not a sealed Kotlin class.")
            null
        }
    }

    private fun processScreenNames(): (List<ScreenHolder>) -> Unit = { holders ->
        if (holders.isNotEmpty()) {
            screenNamesExtension(holders)
        }
    }

    // TODO: Try to remove Pair from this
    private fun processAnalyticsEvents(elementHolderPair: Pair<TypeElement, Map<EventHolder, List<EventParameterHolder>>>) =
        when (elementHolderPair.second.values.isEmpty()) {
            true -> showWarning("${elementHolderPair.first} has no valid inner class.")
            false -> analyticsEventsExtension(className = elementHolderPair.first.asClassName(), holders = elementHolderPair.second)
        }

    // TODO: Try to remove Pair from this
    private fun processUserProperties(elementHolderPair: Pair<TypeElement, Map<PropertyHolder, List<PropertyParameterHolder>>>) =
        when (elementHolderPair.second.values.isEmpty()) {
            true -> showWarning("${elementHolderPair.first} has no valid inner class.")
            false -> userPropertiesExtension(className = elementHolderPair.first.asClassName(), holders = elementHolderPair.second)
        }

    private fun screenNamesExtension(screenHolders: List<ScreenHolder>) {
        generatedDir?.let { outputDir ->
            screenNameSpec {
                outputDir(outputDir)
                holders(screenHolders)
                packageName("co.infinum.collar")
                typeElementValidator(typeElementValidator)
            }
        } ?: run {
            showError("Cannot find generated output dir.")
        }
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

    private fun userPropertiesExtension(className: ClassName, holders: Map<PropertyHolder, List<PropertyParameterHolder>>) {
        generatedDir?.let { outputDir ->
            userPropertiesSpec {
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

    private fun mapElementToPropertyHolder(propertiesElement: TypeElement): Map<PropertyHolder, List<PropertyParameterHolder>> {
        val userProperties = mutableMapOf<PropertyHolder, List<PropertyParameterHolder>>()

        val enclosedElements = propertiesElement.enclosedElements

        if (enclosedElements.size > processorOptions.maxPropertiesCount) {
            showError("You can report up to ${processorOptions.maxPropertiesCount} different user properties per app. Current size is ${enclosedElements.size}.")
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
                    showWarning("$element does not extend from $propertiesElement.")
                    continue
                }

                val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata

                // Make use of KotlinPoet's ClassName to easily get the class' name.
                val propertyClass = element.asClassName()
                val propertyClassSimpleName = annotationProvider.propertyName(element)

                if (propertyClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                    showError("Property name may only contain alphanumeric characters and underscores (\"_\"). $propertyClassSimpleName does not.")
                    continue
                }
                if (processorOptions.reservedProperties.any { propertyClassSimpleName.equals(it, false) }) {
                    showError("The ${processorOptions.reservedProperties.joinToString { "\"$it\"" }} user properties are reserved and cannot be used in ${propertyClassSimpleName}.")
                    continue
                }

                // Extract the primary constructor and its parameters as the property's parameters.
                val proto = kotlinMetadata.data.classProto
                val nameResolver = kotlinMetadata.data.nameResolver

                if (proto.constructorCount == 0) {
                    showWarning("$element has no constructor.")
                    continue
                }

                val propertyParameters = proto.constructorList.first().valueParameterList
                if (propertyParameters.size > 1) {
                    showError("You can associate up to 1 unique parameter with each user property. Current size is ${propertyParameters.size}.")
                } else {
                    val mapKey = PropertyHolder(className = propertyClass, resolvedName = propertyClassSimpleName)
                    val mapValue = propertyParameters.map { valueParameter ->
                        val parameterName = nameResolver.getString(valueParameter.name)
                        PropertyParameterHolder(
                            variableName = parameterName,
                            resolvedName = annotationProvider.propertyParameterName(element, parameterName)
                        )
                    }
                    userProperties[mapKey] = mapValue
                }
            }
        }

        return userProperties
    }
}