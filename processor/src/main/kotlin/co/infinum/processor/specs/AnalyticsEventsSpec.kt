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
        private const val DEFAULT_SIMPLE_NAME = "AnalyticsEvents"
        private const val FUNCTION_NAME_TRACK_EVENT = "trackEvent"
        private const val PARAMETER_NAME_EVENT = "event"

        // TODO: Move away from this function to more manual work of just bundle for 1 less dependency
        private val FUNCTION_BUNDLE_OF = ClassName("androidx.core.os", "bundleOf")
//        private val CLASS_BUNDLE = ClassName("android.os", "Bundle")
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
        build()
    }

    override fun extensions(): List<FunSpec> =
        listOf(
            FunSpec.builder(FUNCTION_NAME_TRACK_EVENT)
                .addParameter(PARAMETER_NAME_EVENT, ClassName(packageName, simpleName))
                .applyIf(holders.isNotEmpty()) {
                    beginControlFlow("when (%L)", PARAMETER_NAME_EVENT)
                    addCode(
                        CodeBlock.builder()
                            .apply {
                                holders.forEach { holder ->
                                    addStatement("is %T -> %T.%L(", holder.className, CLASS_COLLAR, FUNCTION_NAME_TRACK_EVENT)
                                    indent()
                                    addStatement("%S,", holder.eventName)
                                        .apply {
                                            if (holder.eventParameters.isEmpty()) {
                                                addStatement("%T()", FUNCTION_BUNDLE_OF)
                                            } else {
                                                addStatement("%T(", FUNCTION_BUNDLE_OF)
                                                indent()
                                                holder.eventParameters
                                                    .filter { parameterHolder -> parameterHolder.enabled }
                                                    .withIndex()
                                                    .forEach {
                                                        addStatement(
                                                            "%S to %L.%L%L",
                                                            it.value.resolvedName,
                                                            PARAMETER_NAME_EVENT,
                                                            it.value.variableName,
                                                            when (it.index) {
                                                                holder.eventParameters.size - 1 -> ""
                                                                else -> ","
                                                            }
                                                        )
                                                    }
                                                unindent()
                                                addStatement(")")
                                            }
                                        }
                                    unindent()
                                    addStatement(")")
                                }
                            }
                            .build()
                    )
                    endControlFlow()
                }
                .build()
        )
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