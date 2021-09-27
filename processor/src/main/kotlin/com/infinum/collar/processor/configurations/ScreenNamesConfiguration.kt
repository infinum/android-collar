package com.infinum.collar.processor.configurations

import com.infinum.collar.processor.options.Options
import javax.annotation.processing.ProcessingEnvironment

internal class ScreenNamesConfiguration(
    processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options? = null
}
