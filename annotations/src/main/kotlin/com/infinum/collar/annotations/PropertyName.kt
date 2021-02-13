package com.infinum.collar.annotations

/**
 * A user property name annotation
 *
 * @property value Holds the actual name of the user property. If empty, actual field name will be taken.
 * @property enabled Determines if this annotation will be processed or skipped.
 * @constructor Default values are provided with an empty value and enabled annotation ready for processing.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class PropertyName(
    val value: String = "",
    val enabled: Boolean = true
)
