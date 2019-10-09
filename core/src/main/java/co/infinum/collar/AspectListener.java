package co.infinum.collar;

import java.util.Map;

import co.infinum.collar.annotations.TrackEvent;

interface AspectListener {

    void onAspectEventTriggered(TrackEvent trackEvent, Map<String, Object> attributes);

    void onAspectSuperAttributeAdded(String key, Object value);

    void onAspectSuperAttributeRemoved(String key);
}