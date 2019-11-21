package co.infinum.collar.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class EventParameterName(val value: String)