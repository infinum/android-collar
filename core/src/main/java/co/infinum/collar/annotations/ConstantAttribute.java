package co.infinum.collar.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Assign a constant value for the given attribute
 * This can be used in the situations that the actual value is not dynamic.
 * For example: A good case would be a button name or any predefined value
 * <p>
 * <pre>
 *   <code>{@literal @}ConstantAttribute(key="event_name", value="Splash")
 *    class Foo {
 *    }
 *   </code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConstantAttribute {

    String key();

    String value();

    /**
     * Keep this event in entire app scope
     */
    boolean isSuper() default false;
}
