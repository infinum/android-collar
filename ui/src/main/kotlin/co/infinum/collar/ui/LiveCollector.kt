package co.infinum.collar.ui

import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen
import co.infinum.collar.ui.data.room.entity.EventEntity
import co.infinum.collar.ui.data.room.entity.PropertyEntity
import co.infinum.collar.ui.data.room.entity.ScreenEntity
import co.infinum.collar.ui.data.room.repository.EntityRepository

open class LiveCollector(
    context: Context,
    val showNotification: Boolean = true
) : Collector {

    private val notificationProvider: NotificationProvider = NotificationProvider(context)

    init {
        EntityRepository.initialize(context)
    }

    @CallSuper
    override fun onScreen(screen: Screen) {
        val entity = ScreenEntity(
            timestamp = System.currentTimeMillis(),
            name = screen.name
        )
        EntityRepository.saveScreen(entity)
        if (showNotification) {
            notificationProvider.showScreen(entity)
        }
    }

    @CallSuper
    override fun onEvent(event: Event) {
        val entity = EventEntity(
            timestamp = System.currentTimeMillis(),
            name = event.name,
            parameters = event.params?.let { bundleToMap(it) }
        )
        EntityRepository.saveEvent(entity)
        if (showNotification) {
            notificationProvider.showEvent(entity)
        }
    }

    @CallSuper
    override fun onProperty(property: Property) {
        val entity = PropertyEntity(
            timestamp = System.currentTimeMillis(),
            name = property.name,
            value = property.value
        )
        EntityRepository.saveProperty(entity)
        if (showNotification) {
            notificationProvider.showProperty(entity)
        }
    }

    private fun bundleToMap(bundle: Bundle): String {
        val map = mutableMapOf<String, String>()

        val ks: Set<String> = bundle.keySet()
        val iterator = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (bundle.getString(key).isNullOrBlank().not()) {
                map[key] = bundle.getString(key).orEmpty()
            }
        }
        return map.toList().joinToString("\n") { "${it.first} = ${it.second}" }
    }
}