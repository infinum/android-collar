package co.infinum.collar.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Maps the actual given attribute value to a custom one.
 * Should be use with {@link ConvertAttribute}
 * For example:
 * <p>
 * Your attribute value might be position index but you want to track an actual value
 * by using convertAttributeMap, tracker will use the defined custom value
 * </p>
 * <pre>
 * <code>
 * class Foo {
 *     {@literal @}TrackEvent("event")
 *     {@literal @}ConvertAttributeMap(
 *        keys = {0, 1},
 *        values = {"value0", "value1"}
 *      )
 *      public void foo(@ConvertAttribute("key") int position) {
 *      }
 * }
 * </code></pre>
 * <p>
 * Collar will use first value as attribute value for first position 0 and other respective order of both arrays
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertAttributes {

    int[] keys();

    String[] values();
}
