package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.ScreenNamesCollector
import co.infinum.processor.configurations.Configuration
import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.options.Options
import co.infinum.processor.specs.screenNameSpec
import co.infinum.processor.validators.ScreenNamesValidator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
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
        val collector = ScreenNamesCollector(roundEnvironment)
        val validator = ScreenNamesValidator(processorOptions, elementUtils, typeUtils, collector, onWarning, onError)

        collector.collect()
            .apply { validator.validate(this) }
            .map {
                ScreenHolder(
                    subClassName = findSubClassName(validator.supported(), (it as TypeElement)),
                    className = it.asClassName(),
                    screenName = collector.name(it)
                )
            }.run {
                if (this.isNotEmpty()) {
                    generatedDir?.let { outputDir ->
                        screenNameSpec {
                            outputDir(outputDir)
                            holders(this@run)
                            packageName("co.infinum.collar")
                        }
                    } ?: run {
                        onError("Cannot find generated output dir.")
                    }
                }
            }
    }

    private fun findSubClassName(allowedTypeElements: List<TypeElement>, element: TypeElement): ClassName? =
        allowedTypeElements.find { element == it }?.asClassName()
}