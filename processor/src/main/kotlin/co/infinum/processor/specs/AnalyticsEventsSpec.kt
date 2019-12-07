package co.infinum.processor.specs

import co.infinum.processor.CollarProcessor
import co.infinum.processor.models.EventHolder
import co.infinum.processor.models.EventParameterHolder
import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.options.ProcessorOptions
import co.infinum.processor.validators.TypeElementValidator
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File

class AnalyticsEventsSpec private constructor(
    outputDir: File,
    private val packageName: String,
    private val simpleName: String,
    private val holders: Map<EventHolder, List<EventParameterHolder>>
) : Spec {

    companion object {
        private val CLASS_COLLAR = ClassName("co.infinum.collar", "Collar")
        private val CLASS_BUNDLE = ClassName("android.os", "Bundle")

        private val FUNCTION_BUNDLE_OF = ClassName("androidx.core.os", "bundleOf")

        private const val FUNCTION_NAME_TRACK_EVENT = "trackEvent"

        private const val PARAMETER_NAME_EVENT = "event"
        private const val PARAMETER_NAME_EVENT_NAME = "name"
        private const val PARAMETER_NAME_PARAMS = "params"

        private const val DEFAULT_PACKAGE_NAME = ""
        private const val DEFAULT_SIMPLE_NAME = "AnalyticsEvents"
    }

    open class Builder(
        private var outputDir: File? = null,
        private var className: ClassName = ClassName(DEFAULT_PACKAGE_NAME, DEFAULT_SIMPLE_NAME),
        private var holders: Map<EventHolder, List<EventParameterHolder>> = mapOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun className(className: ClassName) = apply { this.className = className }
        fun holders(holders: Map<EventHolder, List<EventParameterHolder>>) = apply { this.holders = holders }
        fun build() = AnalyticsEventsSpec(outputDir!!, className.packageName, className.simpleName, holders)
    }

    init {
        build().writeTo(outputDir)
    }

    override fun file(): FileSpec =
        FileSpec.builder(packageName, simpleName)
            .addAnnotation(jvmName())
            .addComment(comment().toString())
            .build()

    override fun jvmName(): AnnotationSpec =
        AnnotationSpec.builder(JvmName::class.java)
            .addMember("%S", "${CLASS_COLLAR.simpleName}$simpleName")
            .build()

    override fun comment(): CodeBlock =
        CodeBlock.builder()
            .addStatement("Converts [%T] to event name and params and logs it using [%T.%L].", ClassName(packageName, simpleName), CLASS_COLLAR, FUNCTION_NAME_TRACK_EVENT)
            .addStatement("")
            .addStatement("This is a generated extension file. Do not edit.")
            .build()

    override fun build(): FileSpec =
        file().toBuilder().apply {
            val extensionFunSpecBuilder = FunSpec.builder(FUNCTION_NAME_TRACK_EVENT)
                .addParameter(PARAMETER_NAME_EVENT, ClassName(packageName, simpleName))
                .addStatement("val %L: %T", PARAMETER_NAME_EVENT_NAME, String::class)
                .addStatement("val %L: %T", PARAMETER_NAME_PARAMS, CLASS_BUNDLE)
                .beginControlFlow("when (%L)", PARAMETER_NAME_EVENT)
            for ((declaredAnalyticsEvent, eventParamList) in holders) {
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

            addFunction(extensionFunSpecBuilder.build())
        }.build()
}

@DslMarker
annotation class AnalyticsEventsSpecDsl

@AnalyticsEventsSpecDsl
class AnalyticsEventsSpecBuilder : AnalyticsEventsSpec.Builder()

inline fun analyticsEventsSpec(builder: AnalyticsEventsSpecBuilder.() -> Unit): AnalyticsEventsSpec {
    val specBuilder = AnalyticsEventsSpecBuilder()
    specBuilder.builder()
    return specBuilder.build()
}