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
import co.infinum.collar.ui.notifications.inapp.InAppNotificationProvider
import co.infinum.collar.ui.notifications.system.SystemNotificationProvider

open class LiveCollector(
    private val context: Context,
    private val showSystemNotification: Boolean = true,
    private val showInAppNotification: Boolean = true
) : Collector {

    private val systemNotificationProvider: SystemNotificationProvider = SystemNotificationProvider(context)
    private val inAppNotificationProvider: InAppNotificationProvider = InAppNotificationProvider(context)

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
        if (showSystemNotification) {
            systemNotificationProvider.showScreen(entity)
        }
        if (showInAppNotification) {
            inAppNotificationProvider.showScreen(entity)
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
        if (showSystemNotification) {
            systemNotificationProvider.showEvent(entity)
        }
        if (showInAppNotification) {
            inAppNotificationProvider.showEvent(entity)
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
        if (showSystemNotification) {
            systemNotificationProvider.showProperty(entity)
        }
        if (showInAppNotification) {
            inAppNotificationProvider.showProperty(entity)
        }
    }
}