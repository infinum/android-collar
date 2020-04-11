package co.infinum.processor.specs

import co.infinum.processor.extensions.applyIf
import co.infinum.processor.models.EventHolder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import java.io.File

class AnalyticsEventsSpec private constructor(
    outputDir: File,
    private val packageName: String,
    private val simpleName: String,
    private val holders: Set<EventHolder>
) : CommonSpec(outputDir, packageName, simpleName) {

    companion object {
        private const val SIMPLE_NAME = "AnalyticsEvents"
        private const val FUNCTION_NAME = "trackEvent"

        private const val STATEMENT_EVENT_CLASS_START = "is %T -> %T.%L("
        private const val STATEMENT_EVENT_CLASS_END = ")"
        private const val STATEMENT_EVENT_NAME = "%S,"
        private const val STATEMENT_BUNDLE_EMPTY = "%T()"
        private const val STATEMENT_BUNDLE_START = "%T("
        private const val STATEMENT_BUNDLE_END = ")"
        private const val STATEMENT_EVENT_PARAMETER = "%S to %L.%L%L"

        // TODO: Move away from this function to more manual work of just bundle for 1 less dependency
        private val FUNCTION_BUNDLE_OF = ClassName("androidx.core.os", "bundleOf")
        private val CLASS_BUNDLE = ClassName("android.os", "Bundle")
    }

    open class Builder(
        private var outputDir: File? = null,
        private var className: ClassName = ClassName(PACKAGE_NAME, SIMPLE_NAME),
        private var holders: Set<EventHolder> = setOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun className(className: ClassName) = apply { this.className = className }
        fun holders(holders: Set<EventHolder>) = apply { this.holders = holders }
        fun build() = AnalyticsEventsSpec(outputDir!!, className.packageName, className.simpleName, holders)
    }

    init {
        build()
    }

    override fun functionName(): String = FUNCTION_NAME

    override fun extensions(): List<FunSpec> =
        listOf(
            FunSpec.builder(functionName())
                .addParameter(parameterName(), ClassName(packageName, simpleName))
                .applyIf(holders.isNotEmpty()) {
                    beginControlFlow(CONTROL_FLOW_WHEN, parameterName())
                    addCode(events())
                    endControlFlow()
                }
                .build()
        )

    private fun events(): CodeBlock =
        CodeBlock.builder()
            .apply {
                holders.forEach { holder ->
                    addStatement(STATEMENT_EVENT_CLASS_START, holder.className, CLASS_COLLAR, functionName())
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
            addStatement(STATEMENT_BUNDLE_START, FUNCTION_BUNDLE_OF)
            indent()
            holder.eventParameters
                .filter { parameterHolder -> parameterHolder.enabled }
                .withIndex()
                .forEach {
                    addStatement(
                        STATEMENT_EVENT_PARAMETER,
                        it.value.resolvedName,
                        parameterName(),
                        it.value.variableName,
                        when (it.index) {
                            holder.eventParameters.size - 1 -> ""
                            else -> ","
                        }
                    )
                }
            unindent()
            addStatement(STATEMENT_BUNDLE_END)
        }

    private fun eventName(builder: CodeBlock.Builder, holder: EventHolder): CodeBlock.Builder =
        builder.addStatement(STATEMENT_EVENT_NAME, holder.eventName)
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