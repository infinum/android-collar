package com.infinum.collar

/**
 * This is the container model for the triggered tracking analytics event.
 */
public data class Event(

    /**
     * Name of the tracked analytics event.
     */
    val name: String,

    /**
     * Optional parameters of the tracked analytics event.
     */
    val params: Map<String, *>? = null,

    /**
     * Optional transient data of the tracked analytics event.
     */
    val transientData: Map<String, *>? = null,
)
