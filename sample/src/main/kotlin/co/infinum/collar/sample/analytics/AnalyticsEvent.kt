package co.infinum.collar.sample.analytics

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName

@AnalyticsEvents
sealed class AnalyticsEvent {
  /**
   * User clicked on login button
   */
  @EventName(value = "loginUser")
  data class LoginUser(
    /**
     * Type of login clicked
     */
    @EventParameterName(value = "languageType")
    val languageType: String
  ) : AnalyticsEvent() {
    enum class LanguageTypeEnum(
      val value: String
    ) {
      KOTLIN("kotlin"),

      JAVA("java");

      override fun toString() = value
    }
  }

  /**
   * We need to check types here
   */
  @EventName(value = "checkTypes")
  data class CheckTypes(
    /**
     * checki if bool is ok
     */
    @EventParameterName(value = "checkBool")
    val checkBool: Boolean,
    /**
     * check if text is ok
     */
    @EventParameterName(value = "checkText")
    val checkText: String,
    /**
     * check if number is ok
     */
    @EventParameterName(value = "checkNumber")
    val checkNumber: Long,
    /**
     * check if decimal is ok
     */
    @EventParameterName(value = "checkDecimal")
    val checkDecimal: Double
  ) : AnalyticsEvent()
}
