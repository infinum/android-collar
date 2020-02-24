package co.infinum.collar.sample

class FooKotlin {

    init {
        trackEvent(AnalyticsEvent.FooKotlin())
    }

    fun trackFoo() {
        trackEvent(AnalyticsEvent.TrackFooKotlin())
    }
}