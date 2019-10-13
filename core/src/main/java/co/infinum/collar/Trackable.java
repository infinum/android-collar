package co.infinum.collar;

import java.util.Map;

import co.infinum.collar.annotations.TrackAttribute;

/**
 * Invoked when {@link TrackAttribute} is set for a method parameter.
 * Given attributes will be used for the caller event
 */
public interface Trackable {

    Map<String, Object> trackableAttributes();
}
