package com.infinum.collar.sample

import android.os.Bundle
import com.infinum.collar.annotations.AnalyticsEvents
import com.infinum.collar.annotations.EventName
import com.infinum.collar.annotations.EventParameterName

@AnalyticsEvents
sealed class AnalyticsEvent {

    @EventName(enabled = true)
    data class Event1(
        val myString: String,
        val myBoolean: Boolean,
        val myByte: Byte,
        val myChar: Char,
        val myDouble: Double,
        val myFloat: Float,
        val myInt: Int,
        val myLong: Long,
        val myShort: Short,
        val myBundle: Bundle
    ) : AnalyticsEvent()

    data class Event2(

        @EventParameterName("my_user_id")
        val uuid: String,

        val dayTime: Int

    ) : AnalyticsEvent()

    @EventName("event3")
    data class EventThree(
        val myString: String,
        val myBoolean: Boolean
    ) : AnalyticsEvent()

    @EventName("event4")
    data class Event4(

        @EventParameterName("my_user_uuid")
        val uuid: String,

        val userType: String

    ) : AnalyticsEvent()

    class Event5 : AnalyticsEvent()
}
