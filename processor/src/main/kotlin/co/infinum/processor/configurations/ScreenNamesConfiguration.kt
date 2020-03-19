package co.infinum.processor.configurations

import co.infinum.processor.options.Options
import co.infinum.processor.options.ScreenNamesOptions
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

class ScreenNamesConfiguration(
    generatedDir: File?,
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(generatedDir, processingEnv) {

    override fun options(): Options = ScreenNamesOptions(processingEnv.options)
}