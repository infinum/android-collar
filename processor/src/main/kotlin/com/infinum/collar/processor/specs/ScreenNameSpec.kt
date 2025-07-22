package com.infinum.collar.processor.specs

import com.infinum.collar.processor.extensions.applyIf
import com.infinum.collar.processor.models.ScreenHolder
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.STRING
import java.io.File

internal class ScreenNameSpec(
    outputDir: File,
    private val holders: Set<ScreenHolder>
) : CommonSpec(outputDir, PACKAGE_NAME, SIMPLE_NAME) {

    companion object {
        private const val SIMPLE_NAME = "ScreenNames"
        private const val FUNCTION_TRACK_SCREEN = "trackScreen"
        private const val PARAMETER_NAME = "this"
        private const val STATEMENT = "is %T -> %T.%L(%S, %L)"
        private const val PARAMETER_NAME_TRANSIENT_DATA = "transientData"
    }

    override fun parameterName(): String = PARAMETER_NAME

    override fun extensions(): List<FunSpec> =
        holders
            .groupBy { it.superClassName }
            .keys
            .filterNotNull()
            .map { it to holders.groupBy { holder -> holder.superClassName }[it].orEmpty() }
            .toMap()
            .map { (keyClass, holders) ->
                FunSpec.builder(FUNCTION_TRACK_SCREEN)
                    .receiver(keyClass)
                    .addParameter(nullableMap())
                    .addAnnotation(AnnotationSpec.builder(JvmOverloads::class).build())
                    .applyIf(holders.isNotEmpty()) {
                        beginControlFlow(CONTROL_FLOW_WHEN, parameterName())
                        addCode(screens(holders))
                        endControlFlow()
                    }
                    .build()
            }

    private fun screens(holders: List<ScreenHolder>): CodeBlock =
        CodeBlock.builder()
            .apply {
                holders.forEach {
                    addStatement(this, it)
                }
                addStatement("else -> Unit")
            }
            .build()

    private fun addStatement(builder: CodeBlock.Builder, holder: ScreenHolder) =
        builder.addStatement(
            STATEMENT,
            holder.className,
            CLASS_COLLAR,
            FUNCTION_TRACK_SCREEN,
            holder.screenName,
            "$PARAMETER_NAME_TRANSIENT_DATA.orEmpty()"
        )

    private fun nullableMap(): ParameterSpec {
        val nullableMapType = MAP.parameterizedBy(STRING, STAR).copy(nullable = true)
        return ParameterSpec.builder(PARAMETER_NAME_TRANSIENT_DATA, nullableMapType)
            .defaultValue("null")
            .build()
    }
}
