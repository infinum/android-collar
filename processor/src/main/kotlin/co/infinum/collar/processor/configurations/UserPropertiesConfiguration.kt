package co.infinum.collar.processor.configurations

import co.infinum.collar.processor.options.Options
import co.infinum.collar.processor.options.UserPropertiesOptions
import javax.annotation.processing.ProcessingEnvironment

internal class UserPropertiesConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = UserPropertiesOptions(processingEnv.options)
}
