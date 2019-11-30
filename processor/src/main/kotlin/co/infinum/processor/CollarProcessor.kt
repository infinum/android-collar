package co.infinum.processor

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.collar.annotations.ScreenName
import co.infinum.processor.extensions.toLowerSnakeCase
import co.infinum.processor.models.EventHolder
import co.infinum.processor.models.EventParameterHolder
import co.infinum.processor.models.ScreenHolder
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class CollarProcessor : KotlinAbstractProcessor(), KotlinMetadataUtils {

    companion object {
        private const val DEFAULT_SIZE_SCREEN_NAME = 36
        private const val DEFAULT_COUNT_MAX_EVENTS = 500
        private const val DEFAULT_COUNT_MAX_EVENT_PARAMETERS = 25
        private const val DEFAULT_SIZE_EVENT_NAME = 40
        private val DEFAULT_RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")

        private const val OPTION_SCREEN_NAME_LENGTH = "screen_name_length"
        private const val OPTION_EVENTS_COUNT = "events_count"
        private const val OPTION_EVENT_PARAMETERS_COUNT = "event_parameters_count"
        private const val OPTION_EVENT_NAME_LENGTH = "event_name_length"
        private const val OPTION_RESERVED_PREFIXES = "reserved_prefixes"

        private val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        private val ANNOTATION_ANALYTICS_EVENTS = AnalyticsEvents::class.java
        private val ANNOTATION_ANALYTICS_EVENT_NAME = EventName::class.java
        private val ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME = EventParameterName::class.java

        private const val PARAMETER_NAME_SCREEN = "screen"
        private const val PARAMETER_NAME_EVENT = "event"
        private const val PARAMETER_NAME_EVENT_NAME = "name"
        private const val PARAMETER_NAME_PARAMS = "params"

        private const val FUNCTION_NAME_TRACK_SCREEN = "trackScreen"
        private const val FUNCTION_NAME_TRACK_EVENT = "trackEvent"

        private val CLASS_LIFECYCLE_OWNER = ClassName("androidx.lifecycle", "LifecycleOwner")
        private val CLASS_ACTIVITY = ClassName("android.app", "Activity")
        private val CLASS_FRAGMENT = ClassName("android.app", "Fragment")
        private val CLASS_SUPPORT_FRAGMENT = ClassName("android.support.v4.app", "Fragment")
        private val CLASS_ANDROIDX_FRAGMENT = ClassName("androidx.fragment.app", "Fragment")


        private val CLASS_LIFECYCLE_LAZY = ClassName("co.infinum.collar", "lifecycleAwareLazy")
        private val CLASS_COLLAR = ClassName("co.infinum.collar", "Collar")

        private val CLASS_BUNDLE = ClassName("android.os", "Bundle")
        private val FUNCTION_BUNDLE_OF = ClassName("androidx.core.os", "bundleOf")
    }

    private var typeLifeCycleOwner: TypeElement? = null
    private var typeActivity: TypeElement? = null
    private var typeFragment: TypeElement? = null
    private var typeSupportFragment: TypeElement? = null
    private var typeAndroidXFragment: TypeElement? = null

    private var maxScreenNameSize = DEFAULT_SIZE_SCREEN_NAME
    private var maxEventsCount = DEFAULT_COUNT_MAX_EVENTS
    private var maxEventParametersCount = DEFAULT_COUNT_MAX_EVENT_PARAMETERS
    private var maxEventNameSize = DEFAULT_SIZE_EVENT_NAME
    private var reservedPrefixes = DEFAULT_RESERVED_PREFIXES

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(
            ANNOTATION_SCREEN_NAME.name,
            ANNOTATION_ANALYTICS_EVENTS.name,
            ANNOTATION_ANALYTICS_EVENT_NAME.name,
            ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME.name
        )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedOptions(): Set<String> =
        setOf(
            OPTION_SCREEN_NAME_LENGTH,
            OPTION_EVENTS_COUNT,
            OPTION_EVENT_PARAMETERS_COUNT,
            OPTION_EVENT_NAME_LENGTH,
            OPTION_RESERVED_PREFIXES
        )

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        with(processingEnv.options) {
            this[OPTION_SCREEN_NAME_LENGTH]?.let { maxScreenNameSize = it.toInt() }
            this[OPTION_EVENTS_COUNT]?.let { maxEventsCount = it.toInt() }
            this[OPTION_EVENT_PARAMETERS_COUNT]?.let { maxEventParametersCount = it.toInt() }
            this[OPTION_EVENT_NAME_LENGTH]?.let { maxEventNameSize = it.toInt() }
            this[OPTION_RESERVED_PREFIXES]?.let { reservedPrefixes = it.split(",") }
        }

        with(processingEnv.elementUtils) {
            typeLifeCycleOwner = getTypeElement("androidx.lifecycle.LifecycleOwner")
            typeActivity = getTypeElement("android.app.Activity")
            typeFragment = getTypeElement("android.app.Fragment")
            typeSupportFragment = getTypeElement("android.support.v4.app.Fragment")
            typeAndroidXFragment = getTypeElement("androidx.fragment.app.Fragment")
        }
    }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val outputDir = generatedDir
        if (outputDir == null) {
            showError("Cannot find generated output dir.")
            return false
        }

        // Get all elements that had been annotated with our annotation
        val annotatedScreenNameElements = roundEnv.getElementsAnnotatedWith(ANNOTATION_SCREEN_NAME)
        val annotatedAnalyticsEventsElements = roundEnv.getElementsAnnotatedWith(ANNOTATION_ANALYTICS_EVENTS)

        val declaredScreenNames = annotatedScreenNameElements
            .mapNotNull { element ->
                val typeElement = resolveTypeElement(element)
                typeElement?.let {
                    ScreenHolder(
                        typeElement = it,
                        className = (element as TypeElement).asClassName(),
                        screenName = element.getAnnotation(ANNOTATION_SCREEN_NAME).value
                    )
                }
            }
            .filterNot { it.screenName.isBlank() }

        if (declaredScreenNames.isEmpty()) {
            showWarning("No valid screen names found.")
        } else {
            generateScreenNamesExtension(declaredScreenNames, outputDir)
        }

        for (annotatedElement in annotatedAnalyticsEventsElements) {
            // Check if the annotatedElement is a Kotlin sealed class
            val analyticsElement = getAnalyticsEventElement(annotatedElement) ?: continue

            // Get all the declared inner class as our Analytics Event
            val declaredAnalyticsEvents = getDeclaredAnalyticsEvents(analyticsElement)

            if (declaredAnalyticsEvents.isEmpty()) {
                // No declared Analytics Event, skip this class.
                showWarning("$analyticsElement has no valid inner class.")
                continue
            }

            // Generate codes with KotlinPoet
            generateAnalyticsEventsExtension(
                analyticsElement,
                declaredAnalyticsEvents,
                outputDir
            )
        }

        return true
    }

    private fun getAnalyticsEventElement(element: Element): TypeElement? {
        val kotlinMetadata = element.kotlinMetadata
        if (kotlinMetadata !is KotlinClassMetadata || element !is TypeElement) {
            // Not a Kotlin class
            showWarning("$element is not a Kotlin class.")
            return null
        }
        val proto = kotlinMetadata.data.classProto
        if (proto.modality != ProtoBuf.Modality.SEALED) {
            // Is not a sealed class
            showWarning("$element is not a sealed Kotlin class.")
            return null
        }
        return element
    }

    private fun generateScreenNamesExtension(declaredScreenNames: List<ScreenHolder>, outputDir: File) {
        val className = ClassName(declaredScreenNames.first().className.topLevelClassName().packageName, "ScreenNames")
        val jvmNameAnnotationSpec = AnnotationSpec.builder(JvmName::class.java).addMember("%S", CLASS_COLLAR.simpleName + "ScreenName")

        val fileSpec = FileSpec.builder(className.packageName, className.simpleName)
            .addAnnotation(jvmNameAnnotationSpec.build())
            .addComment(codeBlockDoc().toString())

        declaredScreenNames.groupBy { it.typeElement }
            .forEach { mapEntry ->
                resolveParameterClass(mapEntry.key)?.let {
                    val extensionFunSpecBuilder = FunSpec.builder(FUNCTION_NAME_TRACK_SCREEN)
                        .addParameter(PARAMETER_NAME_SCREEN, it)
                        .beginControlFlow("val name = when (%L) {", PARAMETER_NAME_SCREEN)

                    val codeBlockBuilder = CodeBlock.builder()
                        .indent()
                    for (screenHolder in mapEntry.value) {
                        if (screenHolder.screenName.length > maxScreenNameSize) {
                            showError("Screen names can be up to $maxScreenNameSize characters long. $screenHolder.screenName is ${screenHolder.screenName.length} long.")
                            continue
                        }
                        codeBlockBuilder.addStatement("is %T -> %S", screenHolder.className, screenHolder.screenName)
                    }
                    codeBlockBuilder.addStatement("else -> null")
                    codeBlockBuilder.unindent()

                    extensionFunSpecBuilder.addCode(codeBlockBuilder.build())
                    extensionFunSpecBuilder.endControlFlow()
                    if (it == CLASS_LIFECYCLE_OWNER) {
                        extensionFunSpecBuilder.addStatement(
                            "%L?.let { %T(%L) { %T.%L(%L, it) } }",
                            PARAMETER_NAME_EVENT_NAME,
                            CLASS_LIFECYCLE_LAZY,
                            PARAMETER_NAME_SCREEN,
                            CLASS_COLLAR,
                            FUNCTION_NAME_TRACK_SCREEN,
                            PARAMETER_NAME_SCREEN
                        )
                    } else if (it == CLASS_ACTIVITY) {
                        extensionFunSpecBuilder.addStatement(
                            "%L?.let { %T.%L(%L, it) }",
                            PARAMETER_NAME_EVENT_NAME,
                            CLASS_COLLAR,
                            FUNCTION_NAME_TRACK_SCREEN,
                            PARAMETER_NAME_SCREEN
                        )
                    } else {
                        extensionFunSpecBuilder.addStatement(
                            "%L?.let { %L.activity?.let { activity -> %T.%L(activity, it) } }",
                            PARAMETER_NAME_EVENT_NAME,
                            PARAMETER_NAME_SCREEN,
                            CLASS_COLLAR,
                            FUNCTION_NAME_TRACK_SCREEN
                        )
                    }

                    fileSpec.addFunction(extensionFunSpecBuilder.build())
                }
            }

        fileSpec
            .build()
            .writeTo(outputDir)
    }

    private fun resolveParameterClass(typeElement: TypeElement): ClassName? =
        when (typeElement) {
            typeLifeCycleOwner -> CLASS_LIFECYCLE_OWNER
            typeActivity -> CLASS_ACTIVITY
            typeAndroidXFragment -> CLASS_ANDROIDX_FRAGMENT
            typeSupportFragment -> CLASS_SUPPORT_FRAGMENT
            typeFragment -> CLASS_FRAGMENT
            else -> null
        }

    private fun getDeclaredAnalyticsEvents(analyticsElement: TypeElement): Map<EventHolder, List<EventParameterHolder>> {
        val analyticsEvents = mutableMapOf<EventHolder, List<EventParameterHolder>>()

        val enclosedElements = analyticsElement.enclosedElements

        if (enclosedElements.size > maxEventsCount) {
            showError("You can report up to $maxEventsCount different events per app. Current size is ${enclosedElements.size}.")
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
                val eventClassSimpleName = if (element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME) == null) {
                    // Make use of KotlinPoet's ClassName to easily get the class' name.
                    eventClass.simpleName.toLowerSnakeCase()
                } else {
                    // Or use annotation value
                    element.getAnnotation(ANNOTATION_ANALYTICS_EVENT_NAME).value
                }

                if (eventClassSimpleName.length > maxEventNameSize) {
                    showError("Event names can be up to $maxEventNameSize characters long. $eventClassSimpleName is ${eventClassSimpleName.length} long.")
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
                if (reservedPrefixes.any { eventClassSimpleName.startsWith(it) }) {
                    showError("The ${reservedPrefixes.joinToString { "\"$it\"" }} prefixes are reserved and cannot be used in ${eventClassSimpleName}.")
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
                if (eventParameters.size > maxEventParametersCount) {
                    showError("You can associate up to $maxEventParametersCount unique parameters with each event. Current size is ${eventParameters.size}.")
                } else {
                    val mapKey = EventHolder(className = eventClass, resolvedName = eventClassSimpleName)
                    val mapValue = eventParameters.map { valueParameter ->
                        val parameterName = nameResolver.getString(valueParameter.name)
                        EventParameterHolder(
                            variableName = parameterName,
                            resolvedName = resolveParameterName(element, parameterName)
                        )
                    }
                    analyticsEvents[mapKey] = mapValue
                }
            }
        }

        return analyticsEvents
    }

    private fun resolveParameterName(element: TypeElement, parameterName: String): String =
        element.enclosedElements
            .first { it.kind == ElementKind.FIELD && it.simpleName.toString() == parameterName }
            .let {
                if (it.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME) == null) {
                    parameterName.toLowerSnakeCase()
                } else {
                    it.getAnnotation(ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME).value
                }
            }

    private fun generateAnalyticsEventsExtension(
        analyticsElement: TypeElement,
        analyticEvents: Map<EventHolder, List<EventParameterHolder>>,
        outputDir: File
    ) {
        val className = analyticsElement.asClassName()
        val extensionFunSpecBuilder = FunSpec.builder(FUNCTION_NAME_TRACK_EVENT)
            .addKdoc(
                CodeBlock.builder()
                    .addStatement(
                        "Converts [%T] to event name and params and logs it using [%T.%L].",
                        className,
                        CLASS_COLLAR,
                        FUNCTION_NAME_TRACK_EVENT
                    )
                    .addStatement("")
                    .addStatement("This is a generated function. Do not edit.")
                    .build()
            )
            .addParameter(PARAMETER_NAME_EVENT, className)
            .addStatement("val %L: %T", PARAMETER_NAME_EVENT_NAME, String::class)
            .addStatement("val %L: %T", PARAMETER_NAME_PARAMS, CLASS_BUNDLE)
            .beginControlFlow("when (%L)", PARAMETER_NAME_EVENT)

        for ((declaredAnalyticsEvent, eventParamList) in analyticEvents) {
            val codeBlock = CodeBlock.builder()
                .addStatement("is %T -> {", declaredAnalyticsEvent.className)
                .indent()
                .addStatement(
                    "%L = %S",
                    PARAMETER_NAME_EVENT_NAME,
                    declaredAnalyticsEvent.resolvedName
                )
                .apply {
                    if (eventParamList.isNotEmpty()) {
                        addStatement("%L = %T(", PARAMETER_NAME_PARAMS, FUNCTION_BUNDLE_OF)
                        indent()
                        for ((index, parameter) in eventParamList.withIndex()) {
                            val size = eventParamList.size
                            val separator = when (index) {
                                size - 1 -> ""
                                else -> ","
                            }
                            addStatement(
                                "%S to %L.%L%L",
                                parameter.resolvedName,
                                PARAMETER_NAME_EVENT,
                                parameter.variableName,
                                separator
                            )
                        }
                        unindent()
                        addStatement(")")
                    } else {
                        addStatement("%L = %T()", PARAMETER_NAME_PARAMS, CLASS_BUNDLE)
                    }
                }
                .unindent()
                .addStatement("}")
                .build()

            extensionFunSpecBuilder.addCode(codeBlock)
        }
        extensionFunSpecBuilder.endControlFlow()
            .addStatement(
                "%T.%L(%L, %L)",
                CLASS_COLLAR,
                FUNCTION_NAME_TRACK_EVENT,
                PARAMETER_NAME_EVENT_NAME,
                PARAMETER_NAME_PARAMS
            )

        val jvmNameAnnotationSpec = AnnotationSpec.builder(JvmName::class.java)
            .addMember("%S", CLASS_COLLAR.simpleName)

        FileSpec.builder(className.packageName, className.simpleName)
            .addAnnotation(jvmNameAnnotationSpec.build())
            .addFunction(extensionFunSpecBuilder.build())
            .build()
            .writeTo(outputDir)
    }

    private fun showError(message: String) = messager.printMessage(Diagnostic.Kind.ERROR, message)

    private fun showWarning(message: String) = messager.printMessage(Diagnostic.Kind.WARNING, message)

    private fun resolveTypeElement(element: Element): TypeElement? {
        val elementType = element.asType()
        return when {
            typeLifeCycleOwner != null && typeUtils.isSubtype(elementType, typeLifeCycleOwner?.asType()) -> typeLifeCycleOwner
            typeActivity != null && typeUtils.isSubtype(elementType, typeActivity?.asType()) -> typeActivity
            typeAndroidXFragment != null && typeUtils.isSubtype(elementType, typeAndroidXFragment?.asType()) -> typeAndroidXFragment
            typeSupportFragment != null && typeUtils.isSubtype(elementType, typeSupportFragment?.asType()) -> typeSupportFragment
            typeFragment != null && typeUtils.isSubtype(elementType, typeFragment?.asType()) -> typeFragment
            else -> {
                showWarning("$element is not eligible as a screen.")
                null
            }
        }
    }

    private fun codeBlockDoc(): CodeBlock =
        CodeBlock.builder()
            .addStatement("Matches classes to screen names and logs it using [%T.%L].", CLASS_COLLAR, FUNCTION_NAME_TRACK_SCREEN)
            .addStatement("")
            .addStatement("This is a generated extension file. Do not edit.")
            .build()
}