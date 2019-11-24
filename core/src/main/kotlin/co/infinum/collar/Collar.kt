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

        fun trackEvent(eventName: String, params: Bundle) {
            INSTANCE?.trackEvent(eventName, params)
        }
    }

    fun trackScreen(activity: Activity, screenName: String) {
        collector.onScreen(
            Screen(
                activity = activity,
                name = screenName
            )
        )
    }

    fun trackEvent(eventName: String, params: Bundle) {
        collector.onEvent(
            Event(
                name = eventName,
                params = if (params.isEmpty) null else params
            )
        )
    }
}