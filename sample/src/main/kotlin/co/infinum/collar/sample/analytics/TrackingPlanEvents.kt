package co.infinum.collar.sample.analytics

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import kotlin.String

@AnalyticsEvents
sealed class TrackingPlanEvents {
  /**
   * User logged into the app
   */
  @EventName(value = "user_log_in")
  data class UserLogIn(
    /**
     * Type of log in
     */
    @EventParameterName(value = "type")
    val type: String,
    /**
     * Source of the log in
     */
    @EventParameterName(value = "source")
    val source: String
  ) : TrackingPlanEvents() {
    enum class TypeEnum(
      val value: String
    ) {
      FACEBOOK("facebook"),

      GOOGLE("google"),

      IN_APP("in app");

      override fun toString() = value
    }

    enum class SourceEnum(
      val value: String
    ) {
      ONBOARDING("onboarding"),

      SETTINGS("settings");

      override fun toString() = value
    }
  }

  /**
   * User opts in or opts out for analytics
   */
  @EventName(value = "analytics_consent")
  data class AnalyticsConsent(
    /**
     * Information if a user gave consent
     */
    @EventParameterName(value = "type")
    val type: String,
    /**
     * Source where a user gave or declined analytics consent
     */
    @EventParameterName(value = "source")
    val source: String
  ) : TrackingPlanEvents() {
    enum class TypeEnum(
      val value: String
    ) {
      OPT_IN("opt in"),

      OPT_OUT("opt out");

      override fun toString() = value
    }

    enum class SourceEnum(
      val value: String
    ) {
      ONBOARDING("onboarding"),

      SETTINGS("settings");

      override fun toString() = value
    }
  }

  /**
   * User successfully completed connection or failed to connect to the Philips network
   */
  @EventName(value = "philips_network_join")
  data class PhilipsNetworkJoin(
    /**
     * Status of joining to the Philips network
     */
    @EventParameterName(value = "status")
    val status: String,
    /**
     * Part of the app from which a user joined Philips network
     */
    @EventParameterName(value = "source")
    val source: String
  ) : TrackingPlanEvents() {
    enum class StatusEnum(
      val value: String
    ) {
      SUCCESS("success"),

      FAIL("fail");

      override fun toString() = value
    }

    enum class SourceEnum(
      val value: String
    ) {
      ONBOARDING("onboarding"),

      HOME("home");

      override fun toString() = value
    }
  }

  /**
   * User successfully completed pairing or failed to pair phone with the coffee machine
   */
  @EventName(value = "machine_phone_pair")
  data class MachinePhonePair(
    /**
     * Status of phone pairing to the Coffee machine
     */
    @EventParameterName(value = "status")
    val status: String,
    /**
     * Source of the pairing
     */
    @EventParameterName(value = "source")
    val source: String
  ) : TrackingPlanEvents() {
    enum class StatusEnum(
      val value: String
    ) {
      SUCCESS("success"),

      FAIL("fail");

      override fun toString() = value
    }

    enum class SourceEnum(
      val value: String
    ) {
      ONBOARDING("onboarding"),

      HOME("home");

      override fun toString() = value
    }
  }

  /**
   * User successfully completed connection or failed connect Coffee machine to the local WiFi
   * network
   */
  @EventName(value = "machine_wifi_connect")
  data class MachineWifiConnect(
    /**
     * Status of the Coffee machine connecting to the WiFi network
     */
    @EventParameterName(value = "status")
    val status: String,
    /**
     * Source of the machine connecting to the WiFi network
     */
    @EventParameterName(value = "source")
    val source: String
  ) : TrackingPlanEvents() {
    enum class StatusEnum(
      val value: String
    ) {
      SUCCESS("success"),

      FAIL("fail");

      override fun toString() = value
    }

    enum class SourceEnum(
      val value: String
    ) {
      ONBOARDING("onboarding"),

      HOME("home");

      override fun toString() = value
    }
  }

  /**
   * User turned on the Coffee machine from the app
   */
  @EventName(value = "machine_turn_on")
  class MachineTurnOn : TrackingPlanEvents()

  /**
   * User turned off the Coffee machine from the app
   */
  @EventName(value = "machine_turn_off")
  class MachineTurnOff : TrackingPlanEvents()

  /**
   * User removed Coffee machine from the app
   */
  @EventName(value = "machine_remove")
  class MachineRemove : TrackingPlanEvents()

  /**
   * User connects or fails to connect Alexa
   */
  @EventName(value = "alexa_connect")
  data class AlexaConnect(
    /**
     * Status of connecting Alexa
     */
    @EventParameterName(value = "status")
    val status: String,
    /**
     * Source of connecting Alexa
     */
    @EventParameterName(value = "source")
    val source: String
  ) : TrackingPlanEvents() {
    enum class StatusEnum(
      val value: String
    ) {
      SUCCESS("success"),

      FAIL("fail");

      override fun toString() = value
    }

    enum class SourceEnum(
      val value: String
    ) {
      ONBOARDING("onboarding"),

      SETTINGS("settings");

      override fun toString() = value
    }
  }
}
