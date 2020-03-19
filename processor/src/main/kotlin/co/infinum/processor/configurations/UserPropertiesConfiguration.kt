package co.infinum.processor.configurations

import co.infinum.processor.options.Options
import co.infinum.processor.options.UserPropertiesOptions
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

class UserPropertiesConfiguration(
    generatedDir: File?,
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(generatedDir, processingEnv) {

    override fun options(): Options = UserPropertiesOptions(processingEnv.options)
}