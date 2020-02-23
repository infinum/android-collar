package co.infinum.collar.sample

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties

@UserProperties
sealed class UserProperty {

    @PropertyName(value = "user_type_corporate")
    data class UserType1(
        val value: String
    ) : UserProperty()

    data class UserTypeRetail(
        val someCoolValue: String
    ) : UserProperty()
}