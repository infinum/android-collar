package co.infinum.collar.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventParameterName(
    val value: String = "",
    val enabled: Boolean = true
)
