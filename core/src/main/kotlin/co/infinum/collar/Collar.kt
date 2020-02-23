package co.infinum.collar

import android.app.Activity
import android.os.Bundle

class Collar private constructor(
    private val collector: Collector
) {
    companion object {

        private var INSTANCE: Collar? = null

        @JvmStatic
        fun attach(collector: Collector): Collar {
            val newInstance = Collar(collector)
            INSTANCE = newInstance
            return newInstance
        }

        fun trackScreen(activity: Activity, screenName: String) {
            INSTANCE?.trackScreen(activity, screenName)
        }

        fun trackScreen(screen: Screen) {
            INSTANCE?.trackScreen(screen)
        }

        fun trackEvent(eventName: String, params: Bundle) {
            INSTANCE?.trackEvent(eventName, params)
        }

        fun trackEvent(event: Event) {
            INSTANCE?.trackEvent(event)
        }

        fun trackProperty(name: String, value: String) {
            INSTANCE?.trackProperty(name, value)
        }

        fun trackProperty(property: Property) {
            INSTANCE?.trackProperty(property)
        }
    }

    fun trackScreen(activity: Activity, screenName: String) =
        collector.onScreen(
            Screen(
                activity = activity,
                name = screenName
            )
        )

    fun trackScreen(screen: Screen) =
        collector.onScreen(screen)

    fun trackEvent(eventName: String, params: Bundle) =
        collector.onEvent(
            Event(
                name = eventName,
                params = if (params.isEmpty) null else params
            )
        )

    fun trackEvent(event: Event) =
        collector.onEvent(event)

    fun trackProperty(name: String, value: String) =
        collector.onProperty(
            Property(
                name = name,
                value = value
            )
        )

    fun trackProperty(property: Property) =
        collector.onProperty(property)
}