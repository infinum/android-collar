package co.infinum.processor

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.processor.extensions.toLowerSnakeCase
import co.infinum.processor.models.DeclaredAnalyticsEvent
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
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class CollarProcessor : KotlinAbstractProcessor(), KotlinMetadataUtils {

    companion object {
        private const val COUNT_MAX_EVENTS = 500
        private const val COUNT_MAX_EVENT_PARAMETERS = 25
        private const val SIZE_EVENT_NAME = 40

        private val RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")

        private val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        private val ANNOTATION_ANALYTICS_EVENTS = AnalyticsEvents::class.java
        private val ANNOTATION_ANALYTICS_EVENT_NAME = EventName::class.java
        private val ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME = EventParameterName::class.java

        private const val PARAMETER_NAME_EVENT = "event"
        private const val PARAMETER_NAME_EVENT_NAME = "name"
        private const val PARAMETER_NAME_PARAMS = "params"

        private const val FUNCTION_NAME_TRACK_EVENT = "trackEvent"

        private val CLASS_COLLAR = ClassName("co.infinum.collar", "Collar")
        private val CLASS_BUNDLE = ClassName("android.os", "Bundle")
        private val FUNCTION_BUNDLE_OF = ClassName("androidx.core.os", "bundleOf")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(
            ANNOTATION_SCREEN_NAME.name,
            ANNOTATION_ANALYTICS_EVENTS.name,
            ANNOTATION_ANALYTICS_EVENT_NAME.name,
            ANNOTATION_ANALYTICS_EVENT_PARAMETER_NAME.name
        )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val outputDir = generatedDir
        if (outputDir == null) {
            showError("Cannot find generated output dir.")
            return false
        }

        // Get all elements that has been annotated with our annotation
        val annotatedElements = roundEnv.getElementsAnnotatedWith(ANNOTATION_ANALYTICS_EVENTS)

        for (annotatedElement in annotatedElements) {
            // Check if the annotatedElement is a Kotlin sealed class
            val analyticsElement = getAnalyticsElement(annotatedElement) ?: continue

            // Get all the declared inner class as our Analytics Event
            val declaredAnalyticsEvents = getDeclaredAnalyticsEvents(analyticsElement)

            if (declaredAnalyticsEvents.isEmpty()) {
                // No declared Analytics Event, skip this class.
                showWarning("$analyticsElement has no valid inner class.")
                continue
            }

            // Generate codes with KotlinPoet
            generateExtension(analyticsElement, declaredAnalyticsEvents, outputDir)
        }
        return true
    }

    private fun getAnalyticsElement(element: Element): TypeElement? {
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

    private fun getDeclaredAnalyticsEvents(analyticsElement: TypeElement): Map<DeclaredAnalyticsEvent, List<String>> {
        val analyticsEvents = mutableMapOf<DeclaredAnalyticsEvent, List<String>>()

        val enclosedElements = analyticsElement.enclosedElements

        if (enclosedElements.size > COUNT_MAX_EVENTS) {
            showWarning("You can report up to $COUNT_MAX_EVENTS different events per app. Current size is ${enclosedElements.size}.")
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

                if (eventClassSimpleName.length > SIZE_EVENT_NAME) {
                    showWarning("Event names can be up to $SIZE_EVENT_NAME characters long. $eventClassSimpleName is ${eventClassSimpleName.length} long.")
                    continue
                }
                if (eventClassSimpleName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                    showWarning("Event name may only contain alphanumeric characters and underscores (\"_\"). $eventClassSimpleName does not.")
                    continue
                }
                if (eventClassSimpleName.first().isLetter().not()) {
                    showWarning("Event name must start with an alphabetic character. ${eventClassSimpleName.first()} in $eventClassSimpleName is not a letter.")
                    continue
                }
                if (RESERVED_PREFIXES.any { eventClassSimpleName.startsWith(it) }) {
                    showWarning("The ${RESERVED_PREFIXES.joinToString { "\"$it\"" }} prefixes are reserved and should not be used like in ${eventClassSimpleName}.")
                    continue
                }

                // Extract the primary constructor and its parameters as the event's parameters.
                val proto = kotlinMetadata.data.classProto
                val nameResolver = kotlinMetadata.data.nameResolver

                if (proto.constructorCount == 0) {
                    showWarning("$element has no constructor.")
                    continue
                }

                val mainConstructor = proto.constructorList[0]
                val eventParameters = mainConstructor.valueParameterList
                if (eventParameters.size > COUNT_MAX_EVENT_PARAMETERS) {
                    showWarning("You can associate up to $COUNT_MAX_EVENT_PARAMETERS unique parameters with each event. Current size is ${eventParameters.size}.")
                } else {
                    analyticsEvents[DeclaredAnalyticsEvent(className = eventClass, resolvedName = eventClassSimpleName)] = eventParameters.map { valueParameter -> nameResolver.getString(valueParameter.name) }
                }
            }
        }

        return analyticsEvents
    }

    private fun generateExtension(
        analyticsElement: TypeElement,
        analyticEvents: Map<DeclaredAnalyticsEvent, List<String>>,
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
                            val separator = if (index == size - 1) {
                                ""
                            } else {
                                ","
                            }
                            addStatement(
                                "%S to %L.%L%L",
                                parameter.toLowerSnakeCase(),
                                PARAMETER_NAME_EVENT,
                                parameter,
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
}