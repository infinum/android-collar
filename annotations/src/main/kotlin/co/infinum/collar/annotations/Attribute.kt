package co.infinum.collar.annotations

/**
 * Provides attributes for the corresponding analytics event.
 *
 *
 * [Attribute] can be used for the following use cases:
 *
 *  *
 * On method signature:
 * When the method returns a value and you want to be able to use this value for attribute,
 * use it on the method signature. For the following case, returned value ("value") will be
 * used for the attribute ("key")
 * `class Foo{
 * @TrackEvent("event_name")
 * @Attribute(key="key")
 * public String foo(){
 * return "value"
 * }
 * }
` *
 *
 *  * On method parameter:
 * When you want to use any method parameter as attribute, annotate them with Attribute
 * For the following example, "name" value will be used for attribute value
 * <pre>
 * `class Foo{
 * @TrackEvent("event_name")
 * public void foo(@Attribute(key="key") String name){
 * }
 * }
` *
</pre> *
 *
 *
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Attribute(
    val value: String, val defaultValue: String = "",
    /**
     * Sets the attribute as super attribute
     */
    val isSuper: Boolean = false
)
