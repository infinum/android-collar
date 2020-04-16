package co.infinum.processor.configurations

import co.infinum.processor.options.Options
import co.infinum.processor.options.ScreenNamesOptions
import javax.annotation.processing.ProcessingEnvironment

class ScreenNamesConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = ScreenNamesOptions(processingEnv.options)
}
