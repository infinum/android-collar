package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.ScreenNamesCollector
import co.infinum.processor.configurations.Configuration
import co.infinum.processor.options.Options
import co.infinum.processor.specs.screenNameSpec
import co.infinum.processor.validators.ScreenNamesValidator
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class ScreenNamesSubprocessor(
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Subprocessor {

    private var generatedDir: File? = null
    private lateinit var processorOptions: Options
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    override fun init(configuration: Configuration) =
        with(configuration) {
            generatedDir = outputDir()
            processorOptions = options()
            elementUtils = elementUtils()
            typeUtils = typeUtils()
        }

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = ScreenNamesCollector(roundEnvironment, elementUtils, typeUtils)
        val validator = ScreenNamesValidator(processorOptions, onWarning, onError)

        collector.collect().run {
            validator.validate(this).also {
                if (it.isNotEmpty()) {
                    generatedDir?.let { outputDir ->
                        screenNameSpec {
                            outputDir(outputDir)
                            holders(it)
                        }
                    } ?: run {
                        onError("Cannot find generated output dir.")
                    }
                }
            }
        }
    }
}