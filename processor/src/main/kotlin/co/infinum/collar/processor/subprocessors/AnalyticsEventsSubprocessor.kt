package co.infinum.collar.processor.subprocessors

import co.infinum.collar.processor.collectors.AnalyticsEventsCollector
import co.infinum.collar.processor.extensions.asClassName
import co.infinum.collar.processor.extensions.showError
import co.infinum.collar.processor.specs.AnalyticsEventsSpec
import co.infinum.collar.processor.validators.AnalyticsEventsValidator
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment

internal class AnalyticsEventsSubprocessor : CommonSubprocessor() {

    @KotlinPoetMetadataPreview
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
