package co.infinum.processor.configurations

import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class CommonConfiguration(
    private val generatedDir: File?,
    private val processingEnv: ProcessingEnvironment
) : Configuration {

    override fun outputDir(): File? = generatedDir

    override fun elementUtils(): Elements = processingEnv.elementUtils

    override fun typeUtils(): Types = processingEnv.typeUtils
}