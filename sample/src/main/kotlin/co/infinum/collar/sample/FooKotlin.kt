package co.infinum.collar.sample

import co.infinum.collar.annotations.AnalyticsEvent

class FooKotlin {

    @AnalyticsEvent("trackFooKotlin")
    fun trackFoo() {
        //do nothing
    }
}