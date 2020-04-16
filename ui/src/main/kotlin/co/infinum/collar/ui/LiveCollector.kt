package co.infinum.collar.ui

import android.content.Context
import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen
import co.infinum.collar.ui.data.room.CollarDatabase
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import co.infinum.collar.ui.data.room.entity.SettingsEntity
import co.infinum.collar.ui.data.room.repository.EntityRepository
import co.infinum.collar.ui.data.room.repository.SettingsRepository
import co.infinum.collar.ui.notifications.inapp.InAppNotificationProvider
import co.infinum.collar.ui.notifications.system.SystemNotificationProvider

open class LiveCollector(
    context: Context,
    showSystemNotifications: Boolean = true,
    showInAppNotifications: Boolean = true
) : Collector {

    private val systemNotificationProvider: SystemNotificationProvider = SystemNotificationProvider(context)
    private val inAppNotificationProvider: InAppNotificationProvider = InAppNotificationProvider(context)

    private var settings = SettingsEntity(
        showSystemNotifications = showSystemNotifications,
        showInAppNotifications = showInAppNotifications
    )

    init {
        CollarDatabase.create(context).also {
            EntityRepository.initialize(it)
            SettingsRepository.initialize(it)

            SettingsRepository.save(settings)
            SettingsRepository.load().observeForever { entity ->
                settings = settings.copy(
                    showSystemNotifications = entity.showSystemNotifications,
                    showInAppNotifications = entity.showInAppNotifications
                )
            }
        }
    }

    @CallSuper
    override fun onScreen(screen: Screen) {
        val entity = CollarEntity(
            type = EntityType.SCREEN,
            name = screen.name
        )
        EntityRepository.saveScreen(entity)
        if (settings.showSystemNotifications) {
            systemNotificationProvider.showScreen(entity)
        }
        if (settings.showInAppNotifications) {
            inAppNotificationProvider.showScreen(entity)
        }
    }

    @CallSuper
    override fun onEvent(event: Event) {
        val entity = CollarEntity(
            type = EntityType.EVENT,
            name = event.name,
            parameters = event.params?.let { BundleMapper.toMap(it) }
        )
        EntityRepository.saveEvent(entity)
        if (settings.showSystemNotifications) {
            systemNotificationProvider.showEvent(entity)
        }
        if (settings.showInAppNotifications) {
            inAppNotificationProvider.showEvent(entity)
        }
    }

    @CallSuper
    override fun onProperty(property: Property) {
        val entity = CollarEntity(
            type = EntityType.PROPERTY,
            name = property.name,
            value = property.value
        )
        EntityRepository.saveProperty(entity)
        if (settings.showSystemNotifications) {
            systemNotificationProvider.showProperty(entity)
        }
        if (settings.showInAppNotifications) {
            inAppNotificationProvider.showProperty(entity)
        }
    }
}
