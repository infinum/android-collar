package co.infinum.collar.sample

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName

@AnalyticsEvents
sealed class AnalyticsEvent {

    class Event1 : AnalyticsEvent()

    data class Event2(

        @EventParameterName("user_id")
        val uuid: String,

        val dayTime: JavaDayTime

    ) : AnalyticsEvent()

    @EventName("event3")
    class EventThree : AnalyticsEvent()

    @EventName("event4")
    data class Event4(

        @EventParameterName("user_uuid")
        val UUID: String,

        val userType: KotlinUserType

    ) : AnalyticsEvent()
}

