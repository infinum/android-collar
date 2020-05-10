package co.infinum.collar.processor.configurations

import co.infinum.collar.processor.options.AnalyticsEventsOptions
import co.infinum.collar.processor.options.Options
import javax.annotation.processing.ProcessingEnvironment

internal class AnalyticsEventsConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = AnalyticsEventsOptions(processingEnv.options)
}
