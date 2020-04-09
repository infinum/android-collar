package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.AnalyticsEventsCollector
import co.infinum.processor.extensions.showError
import co.infinum.processor.specs.analyticsEventsSpec
import co.infinum.processor.validators.AnalyticsEventsValidator
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment

class AnalyticsEventsSubprocessor : CommonSubprocessor() {

    @KotlinPoetMetadataPreview
    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = AnalyticsEventsCollector(roundEnvironment)
        val validator = AnalyticsEventsValidator(processorOptions, typeUtils, messager)

        collector.collect().run {
            validator.validate(this).forEach {
                generatedDir?.let { outputDir ->
                    analyticsEventsSpec {
                        outputDir(outputDir)
                        className(it.rootClass.asClassName())
                        holders(it.eventHolders)
                    }
                } ?: run {
                    messager.showError("Cannot find generated output dir.")
                }
            }
        }
    }
}