package co.infinum.processor.configurations

import co.infinum.processor.options.Options
import co.infinum.processor.options.UserPropertiesOptions
import javax.annotation.processing.ProcessingEnvironment

internal class UserPropertiesConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = UserPropertiesOptions(processingEnv.options)
}
