package co.infinum.processor

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.ScreenName
import co.infinum.processor.extensions.toLowerSnakeCase
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
        private val ANNOTATION_SCREEN_NAME = ScreenName::class.java
        private val ANNOTATION_ANALYTICS_EVENTS = AnalyticsEvents::class.java

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
            ANNOTATION_ANALYTICS_EVENTS.name
        )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val outputDir = generatedDir
        if (outputDir == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Cannot find generated output dir.")
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
                messager.printMessage(Diagnostic.Kind.WARNING, "$analyticsElement has no valid inner class.")
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
            messager.printMessage(Diagnostic.Kind.WARNING, "$element is not a Kotlin class.")
            return null
        }
        val proto = kotlinMetadata.data.classProto
        if (proto.modality != ProtoBuf.Modality.SEALED) {
            // Is not a sealed class
            messager.printMessage(Diagnostic.Kind.WARNING, "$element is not a sealed Kotlin class.")
            return null
        }
        return element
    }

    private fun getDeclaredAnalyticsEvents(
        analyticsElement: TypeElement
    ): Map<ClassName, List<String>> {
        val analyticsEvents = mutableMapOf<ClassName, List<String>>()

        val enclosedElements = analyticsElement.enclosedElements

        val supertype = analyticsElement.asType()

        for (element in enclosedElements) {

            val type = element.asType()

            if (element !is TypeElement) {
                // Inner element is not a class
                //messager.printMessage(Diagnostic.Kind.WARNING, "$element is not a kotlin class.A")
                continue
            } else if (!typeUtils.directSupertypes(type).contains(supertype)) {
                // Inner class does not extend from the enclosing sealed class
                messager.printMessage(Diagnostic.Kind.WARNING, "$element does not extend from $analyticsElement.")
                continue
            }
            val kotlinMetadata = element.kotlinMetadata as KotlinClassMetadata

            // Make use of KotlinPoet's ClassName to easily get the class' name.
            val eventClass = element.asClassName()

            // Extract the primary constructor and its parameters as the event's parameters.
            val proto = kotlinMetadata.data.classProto
            val nameResolver = kotlinMetadata.data.nameResolver

            if (proto.constructorCount == 0) {
                messager.printMessage(Diagnostic.Kind.WARNING, "$element has no constructor.")
                continue
            }

            val mainConstructor = proto.constructorList[0]
            val eventParameters = mainConstructor.valueParameterList
                .map { valueParameter ->
                    // Resolve the constructor parameter's name
                    // using nameResolver.
                    nameResolver.getString(valueParameter.name)
                }

            analyticsEvents[eventClass] = eventParameters
        }
        return analyticsEvents
    }

    private fun generateExtension(
        analyticsElement: TypeElement,
        analyticEvents: Map<ClassName, List<String>>,
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

        for ((eventName, eventParamList) in analyticEvents) {
            val codeBlock = CodeBlock.builder()
                .addStatement("is %T -> {", eventName)
                .indent()
                .addStatement(
                    "%L = %S",
                    PARAMETER_NAME_EVENT_NAME,
                    eventName.simpleName.toLowerSnakeCase()
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
}