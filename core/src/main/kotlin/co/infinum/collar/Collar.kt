package co.infinum.collar

import android.os.Bundle

/**
 * Singleton object entry point for screen names, events and properties collection.
 */
object Collar {

    private var collector: Collector? = null

    /**
     * Attach a collector to running process.
     *
     * @param collector interface or class implementing such interface.
     */
    @JvmStatic
    fun attach(collector: Collector) {
        this.collector = collector
    }

    /**
     * Set the current analytics collection status (enabled or disabled).
     *
     * @param enabled is the analytics collection currently enabled.
     */
    fun setAnalyticsCollectionStatus(enabled: Boolean) {
        collector?.setAnalyticsCollectionEnabled(enabled = enabled)
    }

    /**
     * Track screen names using a direct value.
     *
     * @param screenName value.
     */
    fun trackScreen(screenName: String) =
        collector?.onScreen(
            Screen(
                name = screenName
            )
        )

    /**
     * Track screen names using a provided wrapper class.
     *
     * @param screen wrapper class.
     */
    fun trackScreen(screen: Screen) =
        collector?.onScreen(screen)

    /**
     * Track events using direct values for event name and optional event parameters.
     *
     * @param eventName value.
     * @param params value.
     */
    fun trackEvent(eventName: String, params: Bundle) =
        collector?.onEvent(
            Event(
                name = eventName,
                params = if (params.isEmpty) null else params
            )
        )

    /**
     * Track events using a provided wrapper class.
     *
     * @param event wrapper class.
     */
    fun trackEvent(event: Event) =
        collector?.onEvent(event)

    /**
     * Track user properties using direct values for property name and optional property value.
     * If value is set as 'null', property is cleared.
     *
     * @param name value.
     * @param value value.
     */
    fun trackProperty(name: String, value: String?) =
        collector?.onProperty(
            Property(
                name = name,
                value = value
            )
        )

    /**
     * Track user properties using a provided wrapper class.
     *
     * @param property wrapper class.
     */
    fun trackProperty(property: Property) =
        collector?.onProperty(property)
}
