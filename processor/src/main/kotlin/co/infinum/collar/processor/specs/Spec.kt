package co.infinum.collar.processor.specs

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

internal interface Spec {

    fun file(): FileSpec

    fun comment(): CodeBlock

    fun jvmName(): AnnotationSpec

    fun parameterName(): String

    fun extensions(): List<FunSpec>

    fun build()
}
