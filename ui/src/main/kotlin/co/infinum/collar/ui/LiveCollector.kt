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
import co.infinum.collar.ui.extensions.redact
import co.infinum.collar.ui.presentation.BundleMapper
import co.infinum.collar.ui.presentation.Presentation
import co.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationProvider
import co.infinum.collar.ui.presentation.notifications.system.SystemNotificationProvider

/**
 * Implementation of Collector interface providing UI for collected screen name, analytics event or user property.
 *
 * @param showSystemNotifications is true by default.
 * @param showInAppNotifications is true by default.
 * @constructor Default values are provided.
 */
open class LiveCollector constructor(
    private val configuration: Configuration = Configuration()
) : Collector {

    private val systemNotificationProvider: SystemNotificationProvider = Presentation.systemNotification()
    private val inAppNotificationProvider: InAppNotificationProvider = Presentation.inAppNotification()

    private var settings = SettingsEntity(
        showSystemNotifications = configuration.showSystemNotifications,
        showInAppNotifications = configuration.showInAppNotifications
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

    /**
     * Invoked when a new screen is emitted.
     *
     * @param screen wrapper class.
     */
    @CallSuper
    override fun onScreen(screen: Screen) {
        val entity = CollarEntity(
            type = EntityType.SCREEN,
            name = screen.name.redact(configuration.redactedKeywords)
        )
        EntityRepository.saveScreen(entity)
        if (settings.showSystemNotifications) {
            systemNotificationProvider.showScreen(entity)
        }
        if (settings.showInAppNotifications) {
            inAppNotificationProvider.showScreen(entity)
        }
    }

    /**
     * Invoked when a new analytics event is emitted.
     *
     * @param event wrapper class.
     */
    @CallSuper
    override fun onEvent(event: Event) {
        val entity = CollarEntity(
            type = EntityType.EVENT,
            name = event.name.redact(configuration.redactedKeywords),
            parameters = event.params?.let { BundleMapper.toMap(it, configuration.redactedKeywords) }
        )
        EntityRepository.saveEvent(entity)
        if (settings.showSystemNotifications) {
            systemNotificationProvider.showEvent(entity)
        }
        if (settings.showInAppNotifications) {
            inAppNotificationProvider.showEvent(entity)
        }
    }

    /**
     * Invoked when a new user property is emitted.
     *
     * @param property wrapper class.
     */
    @CallSuper
    override fun onProperty(property: Property) {
        val entity = CollarEntity(
            type = EntityType.PROPERTY,
            name = property.name.redact(configuration.redactedKeywords),
            value = property.value?.redact(configuration.redactedKeywords)
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
