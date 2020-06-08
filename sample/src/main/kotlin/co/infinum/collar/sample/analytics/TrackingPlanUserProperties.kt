package co.infinum.collar.sample.analytics

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import kotlin.String

@UserProperties
sealed class TrackingPlanUserProperties {
  /**
   * Model of the Coffee machine (e.g. Philips 3200 LatteGo)
   */
  @PropertyName(value = "machine_model")
  data class MachineModel(
    val value: String
  ) : TrackingPlanUserProperties()

  /**
   * User segmentation by the analytics consent (opt in/opt out). Should be set and updated in two
   * locations, onboarding and settings
   */
  @PropertyName(value = "analytics_consent")
  data class AnalyticsConsent(
    val value: String
  ) : TrackingPlanUserProperties() {
    enum class AnalyticsConsentEnum(
      val value: String
    ) {
      OPT_IN("opt in"),

      OPT_OUT("opt out");

      override fun toString() = value
    }
  }
}
