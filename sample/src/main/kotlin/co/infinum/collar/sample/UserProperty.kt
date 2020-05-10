package co.infinum.collar.sample

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties

@UserProperties
sealed class UserProperty {

    @PropertyName(enabled = true)
    data class LanguageType(
        val value: String
    ) : UserProperty()

    @PropertyName(value = "my_id", enabled = true)
    data class MyUUID(
        val uuid: String
    ) : UserProperty()
}
