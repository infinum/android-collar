package co.infinum.collar.annotations

/**
 * Should be used with [ConvertAttributes]
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ConvertAttribute(
    val value: String, val defaultValue: String = "",
    /**
     * Keep this event in entire app scope
     */
    val isSuper: Boolean = false
)
