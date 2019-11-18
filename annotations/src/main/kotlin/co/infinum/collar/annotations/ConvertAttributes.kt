package co.infinum.collar.annotations

/**
 * Maps the actual given attribute value to a custom one.
 * Should be use with [ConvertAttribute]
 * For example:
 *
 *
 * Your attribute value might be position index but you want to track an actual value
 * by using convertAttributeMap, tracker will use the defined custom value
 *
 * <pre>
 * `
 * class Foo {
 * @TrackEvent("event")
 * @ConvertAttributeMap(
 * keys = {0, 1},
 * values = {"value0", "value1"}
 * )
 * public void foo(@ConvertAttribute("key") int position) {
 * }
 * }
`</pre> *
 *
 *
 * Collar will use first value as attribute value for first position 0 and other respective order of both arrays
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ConvertAttributes(val keys: IntArray, val values: Array<String>)
