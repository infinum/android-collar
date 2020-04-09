package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.UserPropertiesCollector
import co.infinum.processor.configurations.Configuration
import co.infinum.processor.options.Options
import co.infinum.processor.specs.userPropertiesSpec
import co.infinum.processor.validators.UserPropertiesValidator
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class UserPropertiesSubprocessor(
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
        val collector = UserPropertiesCollector(roundEnvironment)
        val validator = UserPropertiesValidator(processorOptions, typeUtils, onWarning, onError)

        collector.collect().run {
            validator.validate(this).forEach {
                generatedDir?.let { outputDir ->
                    userPropertiesSpec {
                        outputDir(outputDir)
                        className(it.rootClassName)
                        holders(it.propertyHolders)
                    }
                } ?: run {
                    onError("Cannot find generated output dir.")
                }
            }
        }
    }
}