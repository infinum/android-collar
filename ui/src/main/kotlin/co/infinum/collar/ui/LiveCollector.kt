package co.infinum.collar.ui

import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.domain.repositories.EntityRepository
import co.infinum.collar.ui.domain.repositories.SettingsRepository
import co.infinum.collar.ui.presentation.BundleMapper
import co.infinum.collar.ui.presentation.Presentation
import co.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationProvider
import co.infinum.collar.ui.presentation.notifications.system.SystemNotificationProvider

open class LiveCollector(
    showSystemNotifications: Boolean = true,
    showInAppNotifications: Boolean = true
) : Collector {

    private val systemNotificationProvider: SystemNotificationProvider = Presentation.systemNotification()
    private val inAppNotificationProvider: InAppNotificationProvider = Presentation.inAppNotification()

    private var settings = SettingsEntity(
        showSystemNotifications = showSystemNotifications,
        showInAppNotifications = showInAppNotifications
    )

    init {
        SettingsRepository.save(settings)
        SettingsRepository.load().observeForever { entity ->
            entity?.let {
                settings = settings.copy(
                    showSystemNotifications = it.showSystemNotifications,
                    showInAppNotifications = it.showInAppNotifications
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
