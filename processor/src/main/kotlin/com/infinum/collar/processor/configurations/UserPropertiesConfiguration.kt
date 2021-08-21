package com.infinum.collar.processor.configurations

import com.infinum.collar.processor.options.Options
import com.infinum.collar.processor.options.UserPropertiesOptions
import javax.annotation.processing.ProcessingEnvironment

internal class UserPropertiesConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = UserPropertiesOptions(processingEnv.options)
}
