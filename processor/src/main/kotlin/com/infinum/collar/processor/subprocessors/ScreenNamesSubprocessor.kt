package com.infinum.collar.processor.subprocessors

import com.infinum.collar.processor.collectors.ScreenNamesCollector
import com.infinum.collar.processor.extensions.showError
import com.infinum.collar.processor.specs.ScreenNameSpec
import com.infinum.collar.processor.validators.ScreenNamesValidator
import javax.annotation.processing.RoundEnvironment

internal class ScreenNamesSubprocessor : CommonSubprocessor() {

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = ScreenNamesCollector(roundEnvironment, elementUtils, typeUtils)
        val validator = ScreenNamesValidator()

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
