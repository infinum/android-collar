package co.infinum.collar.annotations

/**
 * Assign a constant value for the given attribute
 * This can be used in the situations that the actual value is not dynamic.
 * For example: A good case would be a button name or any predefined value
 *
 *
 * <pre>
 * `@ConstantAttribute(key="event_name", value="Splash")
 * class Foo {
 * }
` *
</pre> *
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ConstantAttribute(
    val value: String, val key: String,
    /**
     * Keep this event in entire app scope
     */
    val isSuper: Boolean = false
)
