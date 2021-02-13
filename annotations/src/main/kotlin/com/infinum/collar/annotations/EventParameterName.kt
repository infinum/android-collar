package com.infinum.collar.annotations

/**
 * An event parameter name annotation
 *
 * @property value Holds the actual name of the event parameter name. If empty, actual field name will be taken.
 * @property enabled Determines if this annotation will be processed or skipped.
 * @constructor Default values are provided with an empty value and enabled annotation ready for processing.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
public annotation class EventParameterName(
    val value: String = "",
    val enabled: Boolean = true
)
