package com.infinum.collar

/**
 * Singleton object entry point for screen names, events and properties collection.
 */
public object Collar {

    private var collector: Collector? = null

    /**
     * Attach a collector to running process.
     *
     * @param collector interface or class implementing such interface.
     */
    @JvmStatic
    public fun attach(collector: Collector) {
        Collar.collector = collector
    }

    /**
     * Set the current analytics collection status (enabled or disabled).
     *
     * @param enabled is the analytics collection currently enabled.
     */
    @JvmStatic
    public fun setAnalyticsCollectionStatus(enabled: Boolean) {
        collector?.setAnalyticsCollectionEnabled(enabled = enabled)
    }

    /**
     * Track screen names using a direct value.
     *
     * @param screenName value.
     */
    @JvmStatic
    public fun trackScreen(screenName: String): Unit =
        collector?.onScreen(
            Screen(
                name = screenName
            )
        ) ?: Unit

    /**
     * Track screen names using a provided wrapper class.
     *
     * @param screen wrapper class.
     */
    @JvmStatic
    public fun trackScreen(screen: Screen): Unit =
        collector?.onScreen(screen) ?: Unit

    /**
     * Track events using direct values for event name and optional event parameters.
     *
     * @param eventName value.
     * @param params value.
     */
    @JvmStatic
    public fun trackEvent(eventName: String, params: Map<String, *>): Unit =
        collector?.onEvent(
            Event(
                name = eventName,
                params = params.ifEmpty { null }
            )
        ) ?: Unit

    /**
     * Track events using a provided wrapper class.
     *
     * @param event wrapper class.
     */
    @JvmStatic
    public fun trackEvent(event: Event): Unit =
        collector?.onEvent(event) ?: Unit

    /**
     * Track user properties using direct values for property name and optional property value.
     * If value is set as 'null', property is cleared.
     *
     * @param name value.
     * @param value value.
     */
    @JvmStatic
    public fun trackProperty(name: String, value: String?): Unit =
        collector?.onProperty(
            Property(
                name = name,
                value = value
            )
        ) ?: Unit

    /**
     * Track user properties using a provided wrapper class.
     *
     * @param property wrapper class.
     */
    @JvmStatic
    public fun trackProperty(property: Property): Unit =
        collector?.onProperty(property) ?: Unit
}
