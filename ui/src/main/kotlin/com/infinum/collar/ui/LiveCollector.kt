package com.infinum.collar.ui

import androidx.annotation.CallSuper
import com.infinum.collar.Collector
import com.infinum.collar.Event
import com.infinum.collar.Property
import com.infinum.collar.Screen
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.data.models.local.SettingsEntity
import com.infinum.collar.ui.di.LibraryComponents
import com.infinum.collar.ui.domain.entities.models.EntityParameters
import com.infinum.collar.ui.domain.settings.models.SettingsParameters
import com.infinum.collar.ui.extensions.redact
import com.infinum.collar.ui.presentation.ParametersMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of Collector interface providing UI for collected screen name, analytics event or user property.
 *
 * @param configuration is instantiated with default values.
 */
public open class LiveCollector(
    private val configuration: Configuration = Configuration()
) : Collector {

    private val systemNotifications = LibraryComponents.presentation().systemNotificationFactory
    private val inAppNotifications = LibraryComponents.presentation().inAppNotificationFactory
    private val entities = LibraryComponents.presentation().entities
    private val settings = LibraryComponents.presentation().settings

    private var settingsEntity = SettingsEntity(
        analyticsCollectionEnabled = configuration.analyticsCollectionEnabled,
        analyticsCollectionTimestamp = System.currentTimeMillis(),
        showSystemNotifications = configuration.showSystemNotifications,
        showInAppNotifications = configuration.showInAppNotifications
    )

    init {
        saveSettings()

        MainScope().launch {
            settings
                .load(SettingsParameters(entity = settingsEntity))
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    settingsEntity = it
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
        settingsEntity = settingsEntity.copy(
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
        saveEntity(
            CollarEntity(
                type = EntityType.SCREEN,
                name = screen.name.redact(configuration.redactedKeywords)
            )
        )
    }

    /**
     * Invoked when a new analytics event is emitted.
     *
     * @param event wrapper class.
     */
    @CallSuper
    override fun onEvent(event: Event) {
        saveEntity(
            CollarEntity(
                type = EntityType.EVENT,
                name = event.name.redact(configuration.redactedKeywords),
                parameters = event.params?.let {
                    ParametersMapper.toRedactedString(it, configuration.redactedKeywords)
                }
            )
        )
    }

    /**
     * Invoked when a new user property is emitted.
     *
     * @param property wrapper class.
     */
    @CallSuper
    override fun onProperty(property: Property) {
        saveEntity(
            CollarEntity(
                type = EntityType.PROPERTY,
                name = property.name.redact(configuration.redactedKeywords),
                value = property.value?.redact(configuration.redactedKeywords)
            )
        )
    }

    private fun saveEntity(entity: CollarEntity) =
        MainScope().launch {
            val result = withContext(Dispatchers.IO) {
                entities.save(
                    EntityParameters(entity = entity)
                )
            }
            showNotification(entity.copy(id = result))
        }

    private fun saveSettings() =
        MainScope().launch {
            withContext(Dispatchers.IO) {
                settings.save(
                    SettingsParameters(entity = settingsEntity)
                )
            }
        }

    private fun showNotification(entity: CollarEntity) {
        if (settingsEntity.showSystemNotifications) {
            when (entity.type) {
                EntityType.SCREEN -> systemNotifications.showScreen(entity)
                EntityType.EVENT -> systemNotifications.showEvent(entity)
                EntityType.PROPERTY -> systemNotifications.showProperty(entity)
                else -> Unit
            }
        }
        if (settingsEntity.showInAppNotifications) {
            when (entity.type) {
                EntityType.SCREEN -> inAppNotifications.showScreen(entity)
                EntityType.EVENT -> inAppNotifications.showEvent(entity)
                EntityType.PROPERTY -> inAppNotifications.showProperty(entity)
                else -> Unit
            }
        }
    }
}
