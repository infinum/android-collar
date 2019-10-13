package co.infinum.collar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import co.infinum.collar.annotations.TrackEvent;

@SuppressWarnings("WeakerAccess")
public class Collar implements AspectListener {

    private final Map<String, Object> superAttributes = new HashMap<>();

    @NotNull
    private final EventCollector eventCollector;

    @Nullable
    private EventLogger eventLogger;

    public static Collar attach(@NotNull EventCollector eventCollector) {
        final Collar collar = new Collar(eventCollector);
        CollarAspect.observe(collar);
        return collar;
    }

    private Collar(@NotNull EventCollector eventCollector) {
        this.eventCollector = eventCollector;
    }

    @Override
    public void onAspectEventTriggered(TrackEvent trackEvent, Map<String, Object> attributes) {
        trackEvent(new Event(trackEvent, attributes, superAttributes));
    }

    @Override
    public void onAspectSuperAttributeAdded(String key, Object value) {
        addSuperAttribute(key, value);
    }

    @Override
    public void onAspectSuperAttributeRemoved(String key) {
        removeSuperAttribute(key);
    }

    public void trackEvent(@NotNull String eventName) {
        trackEvent(new Event(eventName, null, null, null, superAttributes));
    }

    public void trackEvent(@NotNull String eventName, Map<String, Object> attributes) {
        trackEvent(new Event(eventName, null, null, attributes, superAttributes));
    }

    private void trackEvent(@NotNull Event event) {
        eventCollector.onEventCollected(event);
        log(event);
    }

    private void log(@NotNull Event event) {
        if (eventLogger == null) {
            return;
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder builder = new StringBuilder()
            .append(event.name)
            .append("-> ")
            .append(event.attributes.toString())
            .append(", super attrs: ")
            .append(superAttributes.toString())
            .append(", filters: ")
            .append(Arrays.toString(event.filters));

        eventLogger.onEventLogged(builder.toString());
    }

    public void setEventLogger(@Nullable EventLogger logger) {
        this.eventLogger = logger;
    }

    /**
     * Allows you to add super attribute without requiring to use annotation
     */
    public void addSuperAttribute(String key, Object value) {
        this.superAttributes.put(key, value);
    }

    /**
     * Allows you to remove super attribute without requiring to use annotation
     */
    public void removeSuperAttribute(String key) {
        this.superAttributes.remove(key);
    }
}