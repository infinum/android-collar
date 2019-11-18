package co.infinum.collar

import android.os.Bundle

class Collar private constructor(
    private val eventCollector: EventCollector
) {
    companion object {

        private var INSTANCE: Collar? = null

        @JvmStatic
        fun attach(eventCollector: EventCollector): Collar {
            val newInstance = Collar(eventCollector)
            INSTANCE = newInstance
            return newInstance
        }

        fun trackEvent(eventName: String, params: Bundle) {
            INSTANCE?.trackEvent(eventName, params)
        }
    }

    fun trackEvent(eventName: String, params: Bundle) {
        val event = Event(
            name = eventName,
            params = if (params.isEmpty) null else params
        )
        eventCollector.onEventCollected(event)
    }
}