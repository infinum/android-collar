package co.infinum.collar.sample

import android.os.Bundle
import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName

@AnalyticsEvents
sealed class AnalyticsEvent {

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

        @EventParameterName("user_id")
        val uuid: String,

        val dayTime: Int

    ) : AnalyticsEvent()

    @EventName("event3")
    data class EventThree(
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

    @EventName("event4")
    data class Event4(

        @EventParameterName("user_uuid")
        val UUID: String,

        val userType: String

    ) : AnalyticsEvent()
}

