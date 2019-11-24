package co.infinum.collar

/**
 * Collar aggregates all tracked analytics events.
 * Once any event is ready, Collar emits the event to the collector.
 *
 * This is a good place to implement your analytics tools such as Firebase, Amplitude, Mixpanel, etc.
 */
interface Collector {

    fun onScreen(screen: Screen)

    fun onEvent(event: Event)
}
