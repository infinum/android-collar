package co.infinum.collar.annotations

/**
 * Assign a constant value for the given attribute
 * This can be used in the situations that the actual value is not dynamic.
 * Common usage on Activity or Fragment, but can be set to any View if needed.
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenName(val value: String)
