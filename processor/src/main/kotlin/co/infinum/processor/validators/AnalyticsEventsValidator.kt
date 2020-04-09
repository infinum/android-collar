package co.infinum.processor.validators

import co.infinum.processor.collectors.AnalyticsEventsCollector
import co.infinum.processor.models.EventHolder
import co.infinum.processor.models.PropertyHolder
import co.infinum.processor.options.Options
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class AnalyticsEventsValidator(
    private val processorOptions: Options,
    private val elementUtils: Elements,
    private val typeUtils: Types,
    private val collector: AnalyticsEventsCollector,
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Validator<EventHolder> {

    override fun validate(elements: Set<EventHolder>): Set<EventHolder> {
        return setOf()
    }
}