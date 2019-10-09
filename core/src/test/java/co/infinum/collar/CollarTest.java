package co.infinum.collar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import co.infinum.collar.annotations.TrackEvent;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("WeakerAccess")
public class CollarTest {

    @Mock EventCollector eventCollector;
    @Mock TrackEvent trackEvent;
    @Captor ArgumentCaptor<Event> eventCaptor;

    private Collar collar;

    @Before
    public void setup() {
        initMocks(this);

        collar = Collar.attach(eventCollector);

        when(eventCollector.toString()).thenReturn("Collar");

        when(trackEvent.value()).thenReturn("event");
        when(trackEvent.filters()).thenReturn(new int[] {1, 2});
    }

    @Test
    public void trackWithoutAnnotation() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key", "value");

        collar.trackEvent("event_name", attributes);

        verify(eventCollector).onEventTracked(eventCaptor.capture());

        assertThat(eventCaptor.getValue().name).isEqualTo("event_name");
        assertThat(eventCaptor.getValue().attributes).containsExactly("key", "value");
    }

    @Test
    public void trackFromAspectEvent() throws NoSuchMethodException {
        class Foo {

            @TrackEvent("event_name")
            public void foo() {
                // some code
            }
        }
        TrackEvent event = Foo.class.getMethod("foo").getAnnotation(TrackEvent.class);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key", "value");

        collar.onAspectEventTriggered(event, attributes);

        verify(eventCollector).onEventTracked(eventCaptor.capture());

        assertThat(eventCaptor.getValue().name).isEqualTo("event_name");
        assertThat(eventCaptor.getValue().attributes).containsExactly("key", "value");
    }

    @Test
    public void trackWithEvent() {
        collar.trackEvent("event_name");

        verify(eventCollector).onEventTracked(eventCaptor.capture());

        assertThat(eventCaptor.getValue().name).isEqualTo("event_name");
        assertThat(eventCaptor.getValue().attributes).isNull();
    }

    @Test
    public void addSuperAttributesToEvent() {
        collar.addSuperAttribute("key1", "value1");
        collar.addSuperAttribute("key2", "value2");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key3", "value3");

        collar.trackEvent("event_name", attributes);

        verify(eventCollector).onEventTracked(eventCaptor.capture());

        assertThat(eventCaptor.getValue().name).isEqualTo("event_name");
        assertThat(eventCaptor.getValue().attributes).containsExactly("key3", "value3");
        assertThat(eventCaptor.getValue().superAttributes).containsExactly("key1", "value1", "key2", "value2");
    }

    @Test
    public void addSuperAttributeFromAspects() {
        collar.onAspectSuperAttributeAdded("key1", "value1");

        collar.trackEvent("event_name");

        verify(eventCollector).onEventTracked(eventCaptor.capture());

        assertThat(eventCaptor.getValue().name).isEqualTo("event_name");
        assertThat(eventCaptor.getValue().superAttributes).containsExactly("key1", "value1");
    }

    @Test
    public void removeSuperAttributes() {
        collar.addSuperAttribute("key1", "value1");
        collar.addSuperAttribute("key2", "value2");
        collar.addSuperAttribute("key3", "value3");

        collar.removeSuperAttribute("key1");
        collar.removeSuperAttribute("key2");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key4", "value4");

        collar.trackEvent("event_name", attributes);

        verify(eventCollector).onEventTracked(eventCaptor.capture());

        assertThat(eventCaptor.getValue().name).isEqualTo("event_name");
        assertThat(eventCaptor.getValue().attributes).containsExactly("key4", "value4");
        assertThat(eventCaptor.getValue().superAttributes).containsExactly("key3", "value3");
    }

    @Test
    public void removeSuperAttributeFromAspects() {
        collar.addSuperAttribute("key1", "value1");
        collar.addSuperAttribute("key2", "value2");

        collar.onAspectSuperAttributeRemoved("key1");

        collar.trackEvent("event_name");

        verify(eventCollector).onEventTracked(eventCaptor.capture());

        assertThat(eventCaptor.getValue().name).isEqualTo("event_name");
        assertThat(eventCaptor.getValue().superAttributes).containsExactly("key2", "value2");
    }

    @Test
    public void log() {
        EventLogger eventLogger = mock(EventLogger.class);
        collar.setEventLogger(eventLogger);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key", "value");

        collar.trackEvent("event", attributes);

        verify(eventLogger).log("event-> {key=value}, super attrs: {}, filters: null");
    }
}