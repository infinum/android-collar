package co.infinum.processor.specs

import co.infinum.processor.models.EventHolder
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
    private val holders: Set<EventHolder>
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
        private var holders: Set<EventHolder> = setOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun className(className: ClassName) = apply { this.className = className }
        fun holders(holders: Set<EventHolder>) = apply { this.holders = holders }
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
            val hasDisabledEvents = holders.any { it.enabled }
            val extensionFunSpecBuilder = FunSpec.builder(FUNCTION_NAME_TRACK_EVENT)
                .addParameter(PARAMETER_NAME_EVENT, ClassName(packageName, simpleName))
                .addStatement("val %L: %T", PARAMETER_NAME_EVENT_NAME, String::class)
                .addStatement("val %L: %T", PARAMETER_NAME_PARAMS, CLASS_BUNDLE)
                .beginControlFlow("when (%L)", PARAMETER_NAME_EVENT)
            holders.forEach { eventHolder: EventHolder ->
                val codeBlock = CodeBlock.builder()
                    .addStatement("is %T -> {", eventHolder.className)
                    .indent()
                    .addStatement(
                        "%L = %S",
                        PARAMETER_NAME_EVENT_NAME,
                        eventHolder.eventName
                    )
                    .apply {
                        if (eventHolder.eventParameters.isNotEmpty()) {
                            addStatement("%L = %T(", PARAMETER_NAME_PARAMS, FUNCTION_BUNDLE_OF)
                            indent()
                            eventHolder.eventParameters.filter { it.enabled }.withIndex().forEach {
                                val size = eventHolder.eventParameters.size
                                val separator = when (it.index) {
                                    size - 1 -> ""
                                    else -> ","
                                }
                                addStatement(
                                    "%S to %L.%L%L",
                                    it.value.resolvedName,
                                    PARAMETER_NAME_EVENT,
                                    it.value.variableName,
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
            if (hasDisabledEvents) {
                extensionFunSpecBuilder.addCode(
                    CodeBlock.builder()
                        .addStatement("else -> {")
                        .indent()
                        .addStatement("%L = %S", PARAMETER_NAME_EVENT_NAME, "")
                        .addStatement("%L = %T()", PARAMETER_NAME_PARAMS, FUNCTION_BUNDLE_OF)
                        .unindent()
                        .addStatement("}")
                        .build()
                )
            }
            extensionFunSpecBuilder.endControlFlow()
            extensionFunSpecBuilder.addCode(
                CodeBlock.builder().apply {
                    if (hasDisabledEvents) {
                        addStatement("if (%L.isNotBlank()) {", PARAMETER_NAME_EVENT_NAME)
                        indent()
                    }
                    addStatement(
                        "%T.%L(%L, %L)",
                        CLASS_COLLAR,
                        FUNCTION_NAME_TRACK_EVENT,
                        PARAMETER_NAME_EVENT_NAME,
                        PARAMETER_NAME_PARAMS
                    )
                    if (hasDisabledEvents) {
                        unindent()
                        addStatement("}")
                    }
                }
                    .build()
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