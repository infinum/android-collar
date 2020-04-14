package co.infinum.collar.ui

import android.content.Context
import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import co.infinum.collar.ui.data.room.repository.EntityRepository

open class LiveCollector(
    private val context: Context,
    private val showNotification: Boolean = true
) : Collector {

    private val notificationProvider: NotificationProvider = NotificationProvider(context)

    init {
        EntityRepository.initialize(context)
    }

    @CallSuper
    override fun onScreen(screen: Screen) {
        val entity = CollarEntity(
            type = EntityType.SCREEN,
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
        val entity = CollarEntity(
            type = EntityType.EVENT,
            timestamp = System.currentTimeMillis(),
            name = event.name,
            parameters = event.params?.let { BundleMapper.toMap(it) }
        )
        EntityRepository.saveEvent(entity)
        if (showNotification) {
            notificationProvider.showEvent(entity)
        }
    }

    @CallSuper
    override fun onProperty(property: Property) {
        val entity = CollarEntity(
            type = EntityType.PROPERTY,
            timestamp = System.currentTimeMillis(),
            name = property.name,
            value = property.value
        )
        EntityRepository.saveProperty(entity)
        if (showNotification) {
            notificationProvider.showProperty(entity)
        }
    }
}