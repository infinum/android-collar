package co.infinum.collar.processor.subprocessors

import co.infinum.collar.processor.collectors.ScreenNamesCollector
import co.infinum.collar.processor.extensions.showError
import co.infinum.collar.processor.specs.ScreenNameSpec
import co.infinum.collar.processor.validators.ScreenNamesValidator
import javax.annotation.processing.RoundEnvironment

internal class ScreenNamesSubprocessor : CommonSubprocessor() {

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = ScreenNamesCollector(roundEnvironment, elementUtils, typeUtils)
        val validator = ScreenNamesValidator(processorOptions, messager)

        collector.collect().run {
            validator.validate(this).also {
                if (it.isNotEmpty()) {
                    generatedDir?.let { outputDir ->
                        ScreenNameSpec(outputDir, it)()
                    } ?: run {
                        messager.showError("Cannot find generated output dir.")
                    }
                }
            }
        }
    }
}
