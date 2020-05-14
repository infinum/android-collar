package co.infinum.collar.ui

import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen

@Suppress("UNUSED_PARAMETER")
open class LiveCollector(
    showSystemNotifications: Boolean = false,
    showInAppNotifications: Boolean = false
) : Collector {

    @CallSuper
    override fun onScreen(screen: Screen) = Unit

    @CallSuper
    override fun onEvent(event: Event) = Unit

    @CallSuper
    override fun onProperty(property: Property) = Unit
}
