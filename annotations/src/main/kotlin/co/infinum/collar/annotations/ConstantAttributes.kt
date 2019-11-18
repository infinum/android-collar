package co.infinum.collar.annotations

/**
 * Before Java 8, the same annotation can only be used once
 * When there is a need to add multiple attributes on older Java versions, use this wrapper
 *
 *
 * <pre>
 * `class Foo {
 * @ConstantAttributes ({
 * @ConstantAttribute(key="key1", value="value1"),
 * @ConstantAttribute(key="key2", value="value2"),
 * })
 * public void init() {}
 * }
` *
</pre> *
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ConstantAttributes(vararg val value: ConstantAttribute)
