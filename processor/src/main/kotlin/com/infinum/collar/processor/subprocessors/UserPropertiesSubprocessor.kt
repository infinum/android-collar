package com.infinum.collar.processor.subprocessors

import com.infinum.collar.processor.collectors.UserPropertiesCollector
import com.infinum.collar.processor.extensions.asClassName
import com.infinum.collar.processor.extensions.showError
import com.infinum.collar.processor.specs.UserPropertiesSpec
import com.infinum.collar.processor.validators.UserPropertiesValidator
import javax.annotation.processing.RoundEnvironment

internal class UserPropertiesSubprocessor : CommonSubprocessor() {

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = UserPropertiesCollector(roundEnvironment)
        val validator = UserPropertiesValidator(processorOptions, typeUtils, messager)

        collector.collect().run {
            validator.validate(this).forEach {
                generatedDir?.let { outputDir ->
                    UserPropertiesSpec(outputDir, it.rootClass.asClassName(), it.propertyHolders)()
                } ?: run {
                    messager.showError("Cannot find generated output dir.")
                }
            }
        }
    }
}
