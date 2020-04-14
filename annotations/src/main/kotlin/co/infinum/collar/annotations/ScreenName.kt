package co.infinum.collar.annotations

/**
 * This can be used in the situations that the actual value is not dynamic.
 * Common usage on Activity or Fragment, but can be set to any View if needed.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenName(
    val value: String = "",
    val enabled: Boolean = true
)
