package co.infinum.collar.sample

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName

@AnalyticsEvents
sealed class AnalyticsEvent {

    data class OnCreate(

        @EventParameterName("SCREEN")
        val screen: String

    ) : AnalyticsEvent()

    class Foo : AnalyticsEvent()

    class TrackFoo : AnalyticsEvent()

    class DoFoo : AnalyticsEvent()

    @EventName("FOO_KOTLIN")
    class FooKotlin : AnalyticsEvent()

    class TrackFooKotlin : AnalyticsEvent()

    class DoFooKotlin : AnalyticsEvent()

    data class OnItemSelected(

        @EventParameterName("itemSelectedIndex")
        val position: Int,

        @EventParameterName("IS_EMPTY")
        val nothingSelected: Boolean
    ) : AnalyticsEvent()
}