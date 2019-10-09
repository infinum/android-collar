package co.infinum.collar.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Assign a constant value for the given attribute
 * This can be used in the situations that the actual value is not dynamic.
 * Common usage on Activity or Fragment, but can be set to any View if needed.
 * For example:
 * <p>
 * <pre>
 *   <code>{@literal @}ScreenName(value="LoginScreen")
 *    class Foo {
 *    }
 *   </code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ScreenName {

    String value();
}
