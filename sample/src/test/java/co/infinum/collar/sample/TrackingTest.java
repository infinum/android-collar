package co.infinum.collar.sample;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class TrackingTest {

    private final Map<String, Event> triggeredEvents = new HashMap<>();

    @Before
    public void setup() {
        Collar.attach(new EventCollector() {
            @Override
            public void onEventCollected(Event event) {
                triggeredEvents.put(event.name, event);
            }
        });
    }

    @Test
    public void confirmKotlinAspects() {
        new FooKotlin().trackFoo();

        assertThat(triggeredEvents).containsKey("event_kotlin");
    }

    @Test
    public void confirmJavaAspects() {
        new Foo().trackFoo();

        assertThat(triggeredEvents).containsKey("event_java");
    }
}