package com.infinum.collar.processor.specs

import com.infinum.collar.processor.extensions.applyIf
import com.infinum.collar.processor.models.PropertyHolder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import java.io.File

internal class UserPropertiesSpec(
    outputDir: File,
    private val className: ClassName,
    private val holders: Set<PropertyHolder>
) : CommonSpec(outputDir, className.packageName, className.simpleName) {

    companion object {
        private const val FUNCTION_TRACK_PROPERTY = "trackProperty"
        private const val PARAMETER_NAME_PROPERTY = "userProperty"
        private const val STATEMENT = "is %T -> %T.%L(%S, %L.%L, %L)"
        private const val STATEMENT_BUNDLE_EMPTY = "mapOf<String,Nothing>()"
    }

    override fun parameterName(): String = PARAMETER_NAME_PROPERTY

    override fun extensions(): List<FunSpec> =
        listOf(
            FunSpec.builder(FUNCTION_TRACK_PROPERTY)
                .addParameter(parameterName(), className)
                .applyIf(holders.isNotEmpty()) {
                    beginControlFlow(CONTROL_FLOW_WHEN, parameterName())
                    addCode(properties())
                    endControlFlow()
                }
                .build()
        )

    private fun properties(): CodeBlock =
        CodeBlock.builder()
            .apply {
                holders.forEach {
                    addStatement(
                        STATEMENT,
                        it.className,
                        CLASS_COLLAR,
                        FUNCTION_TRACK_PROPERTY,
                        it.propertyName,
                        parameterName(),
                        it.propertyParameterNames.first(),
                        transientData(it)
                    )
                }
                addStatement("else -> Unit")
            }
            .build()

    private fun transientData(holder: PropertyHolder): CodeBlock {
        val hasTransientData = holder.propertyParameterNames.size > 1
        return if (hasTransientData) {
            CodeBlock.of("${parameterName()}.${holder.propertyParameterNames.last()}")
        } else {
            CodeBlock.of(STATEMENT_BUNDLE_EMPTY)
        }
    }
}
