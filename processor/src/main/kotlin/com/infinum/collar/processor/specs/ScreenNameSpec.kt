package com.infinum.collar.processor.specs

import com.infinum.collar.processor.extensions.applyIf
import com.infinum.collar.processor.models.ScreenHolder
import com.infinum.collar.processor.shared.Constants.CLASS_FRAGMENT
import com.infinum.collar.processor.shared.Constants.CLASS_SUPPORT_FRAGMENT
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File

internal class ScreenNameSpec(
    outputDir: File,
    private val holders: Set<ScreenHolder>
) : CommonSpec(outputDir, PACKAGE_NAME, SIMPLE_NAME) {

    companion object {
        private const val SIMPLE_NAME = "ScreenNames"
        private const val FUNCTION_TRACK_SCREEN = "trackScreen"
        private const val PARAMETER_NAME = "this"
        private const val STATEMENT = "is %T -> %T.%L(%S)"
    }

    override fun file(): FileSpec =
        super.file().toBuilder(PACKAGE_NAME, SIMPLE_NAME)
            .applyIf(hasDeprecatedClasses(holders)) { addAnnotation(suppressDeprecation()) }
            .build()

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
            }
            .build()

    private fun hasDeprecatedClasses(holders: Set<ScreenHolder>): Boolean =
        holders
            .groupBy { it.superClassName }
            .map { it.key }
            .filterNotNull()
            .any { it == CLASS_SUPPORT_FRAGMENT || it == CLASS_FRAGMENT }

    private fun suppressDeprecation(): AnnotationSpec =
        AnnotationSpec.builder(Suppress::class.java)
            .addMember(CodeBlock.of("%S", "DEPRECATION"))
            .build()

    private fun addStatement(builder: CodeBlock.Builder, holder: ScreenHolder) =
        builder.addStatement(
            STATEMENT,
            holder.className,
            CLASS_COLLAR,
            FUNCTION_TRACK_SCREEN,
            holder.screenName
        )
}
