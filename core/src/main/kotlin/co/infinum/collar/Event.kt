package co.infinum.collar

import android.os.Bundle

/**
 * This is the container model for the triggered tracking analytics event.
 */
data class Event(
    val name: String,
    val params: Bundle? = null
)
