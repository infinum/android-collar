package co.infinum.collar.sample

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName

@AnalyticsEvents
sealed class AnalyticsEvent {
    data class OnCreate(
        @EventParameterName("NIKA")
        val screen: String
    ) : AnalyticsEvent()
    class Foo : AnalyticsEvent()
    class TrackFoo : AnalyticsEvent()
    class DoFoo : AnalyticsEvent()
    @EventName("BOJAN")
    class FooKotlin : AnalyticsEvent()
    class TrackFooKotlin : AnalyticsEvent()
    class DoFooKotlin : AnalyticsEvent()
    data class OnItemSelected(val position: Int) : AnalyticsEvent()
}