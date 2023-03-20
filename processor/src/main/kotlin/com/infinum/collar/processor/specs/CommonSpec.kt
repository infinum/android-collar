package com.infinum.collar.processor.specs

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import java.io.File

internal abstract class CommonSpec(
    private val outputDir: File,
    private val packageName: String,
    private val simpleName: String
) : Spec {

    companion object {
        internal const val PACKAGE_NAME = "com.infinum.collar"

        internal const val PARAMETER_NAME = "value"

        internal val CLASS_COLLAR = ClassName(PACKAGE_NAME, "Collar")

        internal const val CONTROL_FLOW_WHEN = "when (%L)"

        private const val COMMENT = "This is a Collar generated extension file. Do not edit manually."

        private val SUPPRESION = setOf("DEPRECATION", "REDUNDANT_ELSE_IN_WHEN")
    }

    override fun file(): FileSpec =
        FileSpec.builder(packageName, simpleName)
            .addAnnotation(jvmName())
            .addAnnotation(suppress())
            .addFileComment(comment().toString())
            .apply { extensions().map { addFunction(it) } }
            .build()

    override fun jvmName(): AnnotationSpec =
        AnnotationSpec.builder(JvmName::class)
            .addMember(CodeBlock.of("%S", "${CLASS_COLLAR.simpleName}$simpleName"))
            .build()

    override fun suppress(): AnnotationSpec =
        AnnotationSpec.builder(Suppress::class)
            .addMember(SUPPRESION.joinToString(", ") { "%S" }, *SUPPRESION.toTypedArray())
            .build()

    override fun comment(): CodeBlock =
        CodeBlock.builder()
            .addStatement(COMMENT)
            .build()

    override fun parameterName(): String = PARAMETER_NAME

    override fun invoke() {
        file().writeTo(outputDir)
    }
}
