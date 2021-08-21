package com.infinum.collar.processor.configurations

import java.io.File
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal abstract class CommonConfiguration(
    private val processingEnv: ProcessingEnvironment
) : Configuration {

    companion object {
        private const val OPTION_KAPT_KOTLIN_GENERATED_DIR = "kapt.kotlin.generated"
    }

    override fun messager(): Messager = processingEnv.messager

    override fun outputDir(): File? = processingEnv.options[OPTION_KAPT_KOTLIN_GENERATED_DIR]?.let(::File)

    override fun elementUtils(): Elements = processingEnv.elementUtils

    override fun typeUtils(): Types = processingEnv.typeUtils
}
