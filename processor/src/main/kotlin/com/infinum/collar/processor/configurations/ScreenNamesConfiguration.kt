package com.infinum.collar.processor.configurations

import com.infinum.collar.processor.options.Options
import com.infinum.collar.processor.options.ScreenNamesOptions
import javax.annotation.processing.ProcessingEnvironment

internal class ScreenNamesConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = ScreenNamesOptions(processingEnv.options)
}
