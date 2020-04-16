package co.infinum.collar.annotations

/**
 * Used in cases when the actual screen name is predefined, provided and static.
 *
 * Should be used only on an Activity, a Fragment or any other variant of the same.
 *
 * ```
 * @ScreenName(value = AnalyticsScreenNames.MAIN_SCREEN)
 * class MainActivity : Activity() {
 *     ...
 * }
 * ```
 *
 * @property value Holds the actual name of the screen. If empty, actual class name will be taken.
 * @property enabled Determines if this annotation will be processed or skipped.
 * @constructor Default values are provided with an empty value and enabled annotation ready for processing.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenName(
    val value: String = "",
    val enabled: Boolean = true
)
