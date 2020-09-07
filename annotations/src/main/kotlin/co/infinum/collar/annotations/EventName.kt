package co.infinum.collar.annotations

/**
 * An analytics event name annotation
 *
 * @property value Holds the actual name of the event. If empty, class name will be taken and formatted into snake case.
 * @property enabled Determines if this annotation will be processed or skipped.
 * @constructor Default values are provided with an empty value and enabled annotation ready for processing.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventName(
    val value: String = "",
    val enabled: Boolean = true
)
