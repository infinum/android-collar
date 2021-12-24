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
        val myBundle: Bundle,
        val myOtherString: String,
        val myOtherBoolean: Boolean,
        val myOtherByte: Byte,
        val myOtherChar: Char,
        val myOtherDouble: Double,
        val myOtherFloat: Float,
        val myOtherInt: Int,
        val myOtherLong: Long,
        val myOtherShort: Short,
        val myBrandString: String,
        val myBrandBoolean: Boolean,
        val myBrandByte: Byte,
        val myBrandChar: Char,
        val myBrandDouble: Double,
        val myBrandFloat: Float,
        val myBrandInt: Int,
        val myBrandLong: Long,
        val myBrandShort: Short,
        val myString2: String,
        val myBoolean2: Boolean,
        val myByte2: Byte,
        val myChar2: Char,
        val myDouble2: Double,
        val myFloat2: Float,
        val myInt2: Int,
        val myLong2: Long,
        val myShort2: Short,
        val myBundle2: Bundle,
        val myOtherString2: String,
        val myOtherBoolean2: Boolean,
        val myOtherByte2: Byte,
        val myOtherChar2: Char,
        val myOtherDouble2: Double,
        val myOtherFloat2: Float,
        val myOtherInt2: Int,
        val myOtherLong2: Long,
        val myOtherShort2: Short,
        val myBrandString2: String,
        val myBrandBoolean2: Boolean,
        val myBrandByte2: Byte,
        val myBrandChar2: Char,
        val myBrandDouble2: Double,
        val myBrandFloat2: Float,
        val myBrandInt2: Int,
        val myBrandLong2: Long,
        val myBrandShort2: Short
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
