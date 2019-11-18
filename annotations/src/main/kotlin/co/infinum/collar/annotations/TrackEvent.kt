package co.infinum.collar.annotations

/**
 * Triggers an analytics event
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class TrackEvent(val value: String, val filters: IntArray = [], val tags: Array<String> = [])