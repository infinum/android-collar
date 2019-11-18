package co.infinum.collar

import co.infinum.collar.annotations.TrackAttribute

/**
 * Invoked when [TrackAttribute] is set for a method parameter.
 * Given attributes will be used for the caller event
 */

interface Trackable {

    fun trackableAttributes(): Map<String, Any>
}
