package co.infinum.collar.sample

import co.infinum.collar.annotations.AnalyticsEvents

@AnalyticsEvents
sealed class AnalyticsEvent {
    data class OnCreate(val screen: String) : AnalyticsEvent()
    class Foo : AnalyticsEvent()
    class TrackFoo : AnalyticsEvent()
    class DoFoo : AnalyticsEvent()
    class FooKotlin : AnalyticsEvent()
    class TrackFooKotlin : AnalyticsEvent()
    class DoFooKotlin : AnalyticsEvent()
    data class OnItemSelected(val position: Int) : AnalyticsEvent()
}