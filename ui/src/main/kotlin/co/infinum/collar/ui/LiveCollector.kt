package co.infinum.collar.ui

import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.di.LibraryKoin
import co.infinum.collar.ui.domain.Repositories
import co.infinum.collar.ui.domain.entities.models.EntityParameters
import co.infinum.collar.ui.domain.settings.models.SettingsParameters
import co.infinum.collar.ui.extensions.redact
import co.infinum.collar.ui.presentation.BundleMapper
import co.infinum.collar.ui.presentation.Presentation
import co.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationProvider
import co.infinum.collar.ui.presentation.notifications.system.SystemNotificationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of Collector interface providing UI for collected screen name, analytics event or user property.
 *
 * @param analyticsCollectionEnabled is true by default.
 * @param showSystemNotifications is true by default.
 * @param showInAppNotifications is true by default.
 */
public open class LiveCollector(
    private val configuration: Configuration = Configuration()
) : Collector {

    private val systemNotificationProvider: SystemNotificationProvider = Presentation.systemNotification()
    private val inAppNotificationProvider: InAppNotificationProvider = Presentation.inAppNotification()

    private var settings = SettingsEntity(
        analyticsCollectionEnabled = configuration.analyticsCollectionEnabled,
        analyticsCollectionTimestamp = System.currentTimeMillis(),
        showSystemNotifications = configuration.showSystemNotifications,
        showInAppNotifications = configuration.showInAppNotifications
    )

    init {
        saveSettings()

        GlobalScope.launch {
            LibraryKoin.koin().get(Repositories.Settings::class)
                .load(SettingsParameters(entity = settings))
                .collectLatest {
                    withContext(Dispatchers.Main) {
                        settings = settings.copy(
                            analyticsCollectionEnabled = it.analyticsCollectionEnabled,
                            analyticsCollectionTimestamp = it.analyticsCollectionTimestamp,
                            showSystemNotifications = it.showSystemNotifications,
                            showInAppNotifications = it.showInAppNotifications
                        )
                    }
                }
        }
    }

    /**
     * Invoked when the analytics connection status changes.
     * This resets the analytics collection timestamp to the current time.
     *
     * @param enabled is analytics collection enabled
     */
    @CallSuper
    override fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        settings = settings.copy(
            analyticsCollectionEnabled = enabled,
            analyticsCollectionTimestamp = System.currentTimeMillis()
        )
        saveSettings()
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
        saveEntity(entity)
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
        saveEntity(entity)
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
        saveEntity(entity)
        if (settings.showSystemNotifications) {
            systemNotificationProvider.showProperty(entity)
        }
        if (settings.showInAppNotifications) {
            inAppNotificationProvider.showProperty(entity)
        }
    }

    private fun saveEntity(entity: CollarEntity) =
        GlobalScope.launch {
            LibraryKoin.koin().get(Repositories.Entity::class)
                .save(
                    EntityParameters(entity = entity)
                )
        }

    private fun saveSettings() =
        GlobalScope.launch {
            LibraryKoin.koin().get(Repositories.Settings::class)
                .save(
                    SettingsParameters(entity = settings)
                )
        }
}
