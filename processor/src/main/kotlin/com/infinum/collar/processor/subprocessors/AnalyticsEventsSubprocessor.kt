package com.infinum.collar.processor.subprocessors

import com.infinum.collar.processor.collectors.AnalyticsEventsCollector
import com.infinum.collar.processor.extensions.asClassName
import com.infinum.collar.processor.extensions.showError
import com.infinum.collar.processor.specs.AnalyticsEventsSpec
import com.infinum.collar.processor.validators.AnalyticsEventsValidator
import javax.annotation.processing.RoundEnvironment

internal class AnalyticsEventsSubprocessor : CommonSubprocessor() {

    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = AnalyticsEventsCollector(roundEnvironment)
        val validator = AnalyticsEventsValidator(processorOptions, typeUtils, messager)

        collector.collect().run {
            validator.validate(this).forEach {
                generatedDir?.let { outputDir ->
                    AnalyticsEventsSpec(outputDir, it.rootClass.asClassName(), it.eventHolders)()
                } ?: run {
                    messager.showError("Cannot find generated output dir.")
                }
            }
        }
    }
}
