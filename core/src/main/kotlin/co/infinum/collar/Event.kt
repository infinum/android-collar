package co.infinum.collar

import java.util.HashMap

import co.infinum.collar.annotations.TrackEvent

/**
 * This is the container model for the triggered tracking analytics event.
 */
class Event {

    val name: String
    val filters: IntArray?
    val tags: Array<String>?
    val attributes: Map<String, Any>?
    val superAttributes: Map<String, Any>
    val allAttributes: Map<String, Any>
        get() {
            val allAttributes = HashMap<String, Any>()
            attributes?.let {
                allAttributes.putAll(it)
            }
            allAttributes.putAll(superAttributes)
            return allAttributes
        }

    constructor(
        eventName: String,
        filters: IntArray? = null,
        tags: Array<String>? = null,
        attributes: Map<String, Any>? = null,
        superAttributes: Map<String, Any> = mapOf()
    ) {
        this.name = eventName
        this.filters = filters
        this.tags = tags
        this.attributes = attributes
        this.superAttributes = superAttributes
    }

    constructor(
        trackEvent: TrackEvent,
        attributes: Map<String, Any>,
        superAttributes: Map<String, Any> = mapOf()
    ) {
        this.name = trackEvent.value
        this.filters = trackEvent.filters
        this.tags = trackEvent.tags
        this.attributes = attributes
        this.superAttributes = superAttributes
    }
}
