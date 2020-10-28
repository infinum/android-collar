package co.infinum.collar

/**
 * Aggregation collector for all tracked screen names, analytics events and user properties.
 * By default, has no implementation or UI.
 *
 * Implementation or invocation of this interface is intended for implementing analytics tools
 * such as Firebase, Amplitude, Mixpanel, etc.
 */
interface Collector {

    /**
     * Controls the collection of Analytics data, which can be either
     * enabled (data is transmitted to the cloud) or disabled (data is only collected locally).
     *
     * @param enabled is the analytics collection currently enabled.
     */
    fun setAnalyticsCollectionEnabled(enabled: Boolean)

    /**
     * Invoked when a new screen is emitted.
     *
     * @param screen wrapper class.
     */
    fun onScreen(screen: Screen)

    /**
     * Invoked when a new analytics event is emitted.
     *
     * @param event wrapper class.
     */
    fun onEvent(event: Event)

    /**
     * Invoked when a new user property is emitted.
     *
     * @param property wrapper class.
     */
    fun onProperty(property: Property)
}
