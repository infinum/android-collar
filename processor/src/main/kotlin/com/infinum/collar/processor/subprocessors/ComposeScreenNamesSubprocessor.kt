package com.infinum.collar.processor.subprocessors

import com.infinum.collar.processor.collectors.ComposeScreenNamesCollector
import com.infinum.collar.processor.extensions.showError
import com.infinum.collar.processor.specs.ComposeScreenNameSpec
import com.infinum.collar.processor.validators.ComposeScreenNamesValidator
import javax.annotation.processing.RoundEnvironment

internal class ComposeScreenNamesSubprocessor : CommonSubprocessor() {

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = ComposeScreenNamesCollector(roundEnvironment, elementUtils, typeUtils)
        val validator = ComposeScreenNamesValidator()

        collector.collect().run {
            validator.validate(this).also {
                generatedDir?.let { outputDir ->
                    it.forEach { holder ->
                        ComposeScreenNameSpec(
                            outputDir = outputDir,
                            packageName = holder.packageName,
                            simpleName = holder.composableName,
                            holder = holder
                        )()
                    }
                } ?: run {
                    messager.showError("Cannot find generated output dir.")
                }
            }
        }
    }
}
