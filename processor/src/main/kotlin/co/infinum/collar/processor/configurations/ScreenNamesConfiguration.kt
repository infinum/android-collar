package co.infinum.collar.processor.configurations

import co.infinum.collar.processor.options.Options
import co.infinum.collar.processor.options.ScreenNamesOptions
import javax.annotation.processing.ProcessingEnvironment

internal class ScreenNamesConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = ScreenNamesOptions(processingEnv.options)
}
