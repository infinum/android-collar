package co.infinum.collar.ui

import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen

/**
 * No operation stub that does nothing.
 * Parameters are unused.
 *
 * @param showSystemNotifications is false by default.
 * @param showInAppNotifications is false by default.
 */
@Suppress("UNUSED_PARAMETER")
open class LiveCollector(
    private val configuration: Configuration = Configuration()
) : Collector {

    /**
     * No operation stub that does nothing.
     *
     * @param screen is unused.
     */
    @CallSuper
    override fun onScreen(screen: Screen) = Unit

    /**
     * No operation stub that does nothing.
     *
     * @param event is unused.
     */
    @CallSuper
    override fun onEvent(event: Event) = Unit

    /**
     * No operation stub that does nothing.
     *
     * @param property is unused.
     */
    @CallSuper
    override fun onProperty(property: Property) = Unit
}
