package co.infinum.collar.sample

import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.annotations.TrackEvent

@ScreenName(value = "FooKotlin")
open class FooKotlin {

    @TrackEvent("event_kotlin")
    open fun trackFoo() {

    }
}