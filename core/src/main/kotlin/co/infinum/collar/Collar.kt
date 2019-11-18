package co.infinum.collar

import java.util.Arrays
import java.util.HashMap

class Collar private constructor(
    private val eventCollector: EventCollector
) {

    companion object {

        @JvmStatic
        fun attach(eventCollector: EventCollector): Collar {
            return Collar(eventCollector)
        }
    }

    private val superAttributes = HashMap<String, Any>()

    private var eventLogger: EventLogger? = null

    fun trackEvent(eventName: String) {
        trackEvent(
            Event(
                eventName = eventName,
                superAttributes = superAttributes
            )
        )
    }

    fun trackEvent(eventName: String, attributes: Map<String, Any>) {
        trackEvent(
            Event(
                eventName = eventName,
                attributes = attributes,
                superAttributes = superAttributes
            )
        )
    }

    private fun trackEvent(event: Event) {
        eventCollector.onEventCollected(event)
        log(event)
    }

    private fun log(event: Event) {
        if (eventLogger == null) {
            return
        }

        val builder = StringBuilder()
            .append(event.name)
            .append("-> ")
            .append(event.attributes.toString())
            .append(", super attrs: ")
            .append(superAttributes.toString())
            .append(", filters: ")
            .append(Arrays.toString(event.filters))

        eventLogger?.onEventLogged(builder.toString())
    }

    fun setEventLogger(logger: EventLogger) {
        this.eventLogger = logger
    }

    /**
     * Allows you to add super attribute without requiring to use annotation
     */
    fun addSuperAttribute(key: String, value: Any) {
        this.superAttributes[key] = value
    }

    /**
     * Allows you to remove super attribute without requiring to use annotation
     */
    fun removeSuperAttribute(key: String) {
        this.superAttributes.remove(key)
    }
}