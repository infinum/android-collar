package co.infinum.collar;

/**
 * Collar aggregates all tracked analytics events and compiles them.
 * Once any event is ready, Collar emits the event to the collector.
 * <p>
 * This is a good place to implement your analytics tools such as Firebase, Amplitude, Mixpanel, etc.
 */
public interface EventCollector {

    void onEventCollected(Event event);
}
