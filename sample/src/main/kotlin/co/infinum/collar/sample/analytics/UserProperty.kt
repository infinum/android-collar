package co.infinum.collar.sample.analytics

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import kotlin.String

@UserProperties
sealed class UserProperty {
  /**
   * user's price
   */
  @PropertyName(value = "price")
  data class Price(
    val value: String
  ) : UserProperty()

  /**
   * ...
   */
  @PropertyName(value = "genderGo")
  data class GenderGo(
    val value: String
  ) : UserProperty() {
    enum class GenderGoEnum(
      val value: String
    ) {
      MALE("male"),

      FEMALE("female"),

      UNKNOWN("unknown");

      override fun toString() = value
    }
  }

  /**
   * ...
   */
  @PropertyName(value = "userType")
  data class UserType(
    val value: String
  ) : UserProperty() {
    enum class UserTypeEnum(
      val value: String
    ) {
      CORPORATE("corporate"),

      RETAIL("retail"),

      UNKNOWN("unknown");

      override fun toString() = value
    }
  }

  /**
   * ...
   */
  @PropertyName(value = "userId")
  data class UserId(
    val value: String
  ) : UserProperty()
}
