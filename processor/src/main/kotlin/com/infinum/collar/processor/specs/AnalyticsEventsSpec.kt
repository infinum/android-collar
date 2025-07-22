package com.infinum.collar.processor.specs

import com.infinum.collar.processor.extensions.applyIf
import com.infinum.collar.processor.models.EventHolder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import java.io.File

internal class AnalyticsEventsSpec(
    outputDir: File,
    private val className: ClassName,
    private val holders: Set<EventHolder>
) : CommonSpec(outputDir, className.packageName, className.simpleName) {

    companion object {
        private const val FUNCTION_TRACK_EVENT = "trackEvent"
        private const val PARAMETER_NAME_EVENT = "event"

        private const val STATEMENT_EVENT_CLASS_START = "is %T -> %T.%L("
        private const val STATEMENT_EVENT_CLASS_END = ")"
        private const val STATEMENT_EVENT_NAME = "%S,"
        private const val STATEMENT_BUNDLE_EMPTY = "mapOf<String,Nothing>()"
        private const val STATEMENT_BUNDLE_EMPTY_WITH_COMMA = "mapOf<String,Nothing>(),"
        private const val STATEMENT_BUNDLE_START = "mapOf("
        private const val STATEMENT_BUNDLE_END = ")"
        private const val STATEMENT_BUNDLE_END_WITH_COMMA = "),"
        private const val STATEMENT_EVENT_PARAMETER = "%S to %L.%L"
    }

    override fun parameterName(): String = PARAMETER_NAME_EVENT

    override fun extensions(): List<FunSpec> =
        listOf(
            FunSpec.builder(FUNCTION_TRACK_EVENT)
                .addParameter(parameterName(), className)
                .applyIf(holders.isNotEmpty()) {
                    beginControlFlow(CONTROL_FLOW_WHEN, parameterName())
                    addCode(events())
                    endControlFlow()
                }
                .build()
        )

    @Suppress("NestedBlockDepth")
    private fun events(): CodeBlock =
        CodeBlock.builder()
            .apply {
                holders.forEach { holder ->
                    addStatement(STATEMENT_EVENT_CLASS_START, holder.className, CLASS_COLLAR, FUNCTION_TRACK_EVENT)
                    indent()
                    eventName(this, holder)
                        .apply {
                            addEventParameters(this, holder)
                            addEventTransientData(this, holder)
                        }
                    unindent()
                    addStatement(STATEMENT_EVENT_CLASS_END)
                }
                addStatement("else -> Unit")
            }
            .build()

    private fun eventParametersEmpty(builder: CodeBlock.Builder): CodeBlock.Builder =
        builder.addStatement(STATEMENT_BUNDLE_EMPTY_WITH_COMMA)

    private fun eventParameters(builder: CodeBlock.Builder, holder: EventHolder): CodeBlock.Builder =
        with(builder) {
            addStatement(STATEMENT_BUNDLE_START)
            indent()
            holder.eventParameters
                .forEachIndexed { index, eventParameterHolder ->
                    addStatement(
                        STATEMENT_EVENT_PARAMETER
                            .plus(",".takeIf { index != holder.eventParameters.size - 1 }.orEmpty()),
                        eventParameterHolder.resolvedName,
                        parameterName(),
                        eventParameterHolder.variableName
                    )
                }
            unindent()
            addStatement(STATEMENT_BUNDLE_END_WITH_COMMA)
        }

    private fun addEventParameters(builder: CodeBlock.Builder, holder: EventHolder) {
        when (holder.eventParameters.isEmpty()) {
            true -> eventParametersEmpty(builder)
            false -> eventParameters(builder, holder)
        }
    }

    private fun addEventTransientData(builder: CodeBlock.Builder, holder: EventHolder) {
        when (holder.eventTransientData.isEmpty()) {
            true -> eventTransientDataEmpty(builder)
            false -> eventTransientData(builder, holder)
        }
    }

    private fun eventName(builder: CodeBlock.Builder, holder: EventHolder): CodeBlock.Builder =
        builder.addStatement(STATEMENT_EVENT_NAME, holder.eventName)

    private fun eventTransientDataEmpty(builder: CodeBlock.Builder): CodeBlock.Builder =
        builder.addStatement(STATEMENT_BUNDLE_EMPTY)

    private fun eventTransientData(builder: CodeBlock.Builder, holder: EventHolder): CodeBlock.Builder =
        with(builder) {
            addStatement(STATEMENT_BUNDLE_START)
            indent()
            holder.eventTransientData
                .forEachIndexed { index, eventTransientDataHolder ->
                    addStatement(
                        STATEMENT_EVENT_PARAMETER
                            .plus(",".takeIf { index != holder.eventTransientData.size - 1 }.orEmpty()),
                        eventTransientDataHolder.resolvedName,
                        parameterName(),
                        eventTransientDataHolder.variableName
                    )
                }
            unindent()
            addStatement(STATEMENT_BUNDLE_END)
        }
}
