package co.infinum.collar.sample

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties

@UserProperties
sealed class UserProperty {

    data class LanguageType(
        val value: String
    ) : UserProperty()

    @PropertyName(value = "my_id")
    data class MyUUID(
        val uuid: String
    ) : UserProperty()
}