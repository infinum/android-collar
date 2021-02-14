package com.infinum.collar

import android.os.Bundle

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
    val params: Bundle? = null
)
