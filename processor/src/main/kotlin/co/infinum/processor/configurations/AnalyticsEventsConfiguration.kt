package co.infinum.processor.configurations

import co.infinum.processor.options.AnalyticsEventsOptions
import co.infinum.processor.options.Options
import javax.annotation.processing.ProcessingEnvironment

class AnalyticsEventsConfiguration(
    private val processingEnv: ProcessingEnvironment
) : CommonConfiguration(processingEnv) {

    override fun options(): Options = AnalyticsEventsOptions(processingEnv.options)
}
