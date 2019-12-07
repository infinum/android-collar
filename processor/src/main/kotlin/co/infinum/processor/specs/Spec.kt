package co.infinum.processor.specs

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import java.io.File

interface Spec {

    fun file(): FileSpec

    fun jvmName(): AnnotationSpec

    fun comment(): CodeBlock

    fun build(): FileSpec
}