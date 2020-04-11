package co.infinum.processor.specs

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File

interface Spec {

    fun file(): FileSpec

    fun comment(): CodeBlock

    fun jvmName(): AnnotationSpec

    fun extensions(): List<FunSpec>

    fun build()
}