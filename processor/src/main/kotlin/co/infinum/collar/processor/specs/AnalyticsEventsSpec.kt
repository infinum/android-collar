package co.infinum.collar.processor.specs

import co.infinum.collar.processor.extensions.applyIf
import co.infinum.collar.processor.models.EventHolder
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
        private const val SIMPLE_NAME = "AnalyticsEvents"
        private const val FUNCTION_TRACK_EVENT = "trackEvent"

        private const val STATEMENT_EVENT_CLASS_START = "is %T -> %T.%L("
        private const val STATEMENT_EVENT_CLASS_END = ")"
        private const val STATEMENT_EVENT_NAME = "%S,"
        private const val STATEMENT_BUNDLE_EMPTY = "%T()"
        private const val STATEMENT_BUNDLE_START = "%T().apply {"
        private const val STATEMENT_BUNDLE_END = "}"
        private const val STATEMENT_EVENT_PARAMETER = "%L(%S, %L.%L)"

        private val CLASS_BUNDLE = ClassName("android.os", "Bundle")
    }

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
                            when (holder.eventParameters.isEmpty()) {
                                true -> eventParametersEmpty(this)
                                false -> eventParameters(this, holder)
                            }
                        }
                    unindent()
                    addStatement(STATEMENT_EVENT_CLASS_END)
                }
            }
            .build()

    private fun eventParametersEmpty(builder: CodeBlock.Builder): CodeBlock.Builder =
        builder.addStatement(STATEMENT_BUNDLE_EMPTY, CLASS_BUNDLE)

    private fun eventParameters(builder: CodeBlock.Builder, holder: EventHolder): CodeBlock.Builder =
        with(builder) {
            addStatement(STATEMENT_BUNDLE_START, CLASS_BUNDLE)
            indent()
            holder.eventParameters
                .filter { parameterHolder -> parameterHolder.enabled }
                .withIndex()
                .forEach {
                    addStatement(
                        STATEMENT_EVENT_PARAMETER,
                        it.value.method,
                        it.value.resolvedName,
                        parameterName(),
                        it.value.variableName
                    )
                }
            unindent()
            addStatement(STATEMENT_BUNDLE_END)
        }

    private fun eventName(builder: CodeBlock.Builder, holder: EventHolder): CodeBlock.Builder =
        builder.addStatement(STATEMENT_EVENT_NAME, holder.eventName)
}
