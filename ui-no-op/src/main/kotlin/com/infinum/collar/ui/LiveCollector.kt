package com.infinum.collar.ui

import androidx.annotation.CallSuper
import com.infinum.collar.Collector
import com.infinum.collar.Event
import com.infinum.collar.Property
import com.infinum.collar.Screen

/**
 * Implementation of Collector interface providing no-op UI.
 *
 * @param configuration is instantiated with default values for no-op.
 */
@Suppress("UNUSED_PARAMETER")
public open class LiveCollector(
    private val configuration: Configuration = Configuration()
) : Collector {

    /**
     * No operation stub that does nothing.
     *
     * @param enabled is unused.
     */
    @CallSuper
    override fun setAnalyticsCollectionEnabled(enabled: Boolean): Unit = Unit

    /**
     * No operation stub that does nothing.
     *
     * @param screen is unused.
     */
    @CallSuper
    override fun onScreen(screen: Screen): Unit = Unit

    /**
     * No operation stub that does nothing.
     *
     * @param event is unused.
     */
    @CallSuper
    override fun onEvent(event: Event): Unit = Unit

    /**
     * No operation stub that does nothing.
     *
     * @param property is unused.
     */
    @CallSuper
    override fun onProperty(property: Property): Unit = Unit
}
