package co.infinum.collar

import android.os.Bundle

object Collar {

    private var collector: Collector? = null

    @JvmStatic
    fun attach(collector: Collector) {
        this.collector = collector
    }

    fun trackScreen(screenName: String) =
        collector?.onScreen(
            Screen(
                name = screenName
            )
        )

    fun trackScreen(screen: Screen) =
        collector?.onScreen(screen)

    fun trackEvent(eventName: String, params: Bundle) =
        collector?.onEvent(
            Event(
                name = eventName,
                params = if (params.isEmpty) null else params
            )
        )

    fun trackEvent(event: Event) =
        collector?.onEvent(event)

    fun trackProperty(name: String, value: String?) =
        collector?.onProperty(
            Property(
                name = name,
                value = value
            )
        )

    fun trackProperty(property: Property) =
        collector?.onProperty(property)
}
