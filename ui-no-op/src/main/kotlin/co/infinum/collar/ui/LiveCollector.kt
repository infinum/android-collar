package co.infinum.collar.ui

import android.content.Context
import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen

open class LiveCollector(
    context: Context,
    private val showNotification: Boolean = false
) : Collector {

    @CallSuper
    override fun onScreen(screen: Screen) {
        // no - op
    }

    @CallSuper
    override fun onEvent(event: Event) {
        // no - op
    }

    @CallSuper
    override fun onProperty(property: Property) {
        // no - op
    }
}