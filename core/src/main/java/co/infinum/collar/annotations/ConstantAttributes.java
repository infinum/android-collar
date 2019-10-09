package co.infinum.collar.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Before Java 8, the same annotation can only be used once
 * When there is a need to add multiple attributes on older Java versions, use this wrapper
 * <p>
 * <pre>
 *   <code>class Foo {
 *    {@literal @}ConstantAttributes ({
 *       {@literal @}ConstantAttribute(key="key1", value="value1"),
 *       {@literal @}ConstantAttribute(key="key2", value="value2"),
 *     })
 *     public void init() {}
 *   }
 *   </code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConstantAttributes {

    ConstantAttribute[] value();
}
