package com.infinum.collar

/**
 * This is the container model for the user property tracking.
 */
public data class Property(

    /**
     * Name of the tracked user property.
     */
    val name: String,

    /**
     * Optional value of the tracked user property.
     */
    val value: String?,

    /**
     * Optional transient data of the tracked user property.
     */
    val transientData: Map<String, *>? = null,
)
