package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.ScreenNamesCollector
import co.infinum.processor.extensions.showError
import co.infinum.processor.specs.screenNameSpec
import co.infinum.processor.validators.ScreenNamesValidator
import javax.annotation.processing.RoundEnvironment

internal class ScreenNamesSubprocessor : CommonSubprocessor() {

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = ScreenNamesCollector(roundEnvironment, elementUtils, typeUtils)
        val validator = ScreenNamesValidator(processorOptions, messager)

        collector.collect().run {
            validator.validate(this).also {
                if (it.isNotEmpty()) {
                    generatedDir?.let { outputDir ->
                        screenNameSpec {
                            outputDir(outputDir)
                            holders(it)
                        }
                    } ?: run {
                        messager.showError("Cannot find generated output dir.")
                    }
                }
            }
        }
    }
}
