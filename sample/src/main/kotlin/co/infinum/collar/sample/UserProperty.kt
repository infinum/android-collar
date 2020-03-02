package co.infinum.collar.sample

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties

@UserProperties
sealed class UserProperty {

    data class LanguageType(
        val value: String
    ) : UserProperty()

    @PropertyName(value = "user_id")
    data class UserUUID(
        val uuid: String
    ) : UserProperty()
}