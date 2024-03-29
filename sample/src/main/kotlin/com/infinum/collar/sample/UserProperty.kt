package com.infinum.collar.sample

import com.infinum.collar.annotations.PropertyName
import com.infinum.collar.annotations.UserProperties

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

    @PropertyName(enabled = false)
    data class UnusedExample(
        val value: String
    ) : UserProperty()
}
