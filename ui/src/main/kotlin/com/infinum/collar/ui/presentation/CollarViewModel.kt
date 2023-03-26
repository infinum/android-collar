package com.infinum.collar.ui.presentation

import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.data.models.local.SettingsEntity
import com.infinum.collar.ui.domain.Repositories
import com.infinum.collar.ui.domain.entities.models.EntityParameters
import com.infinum.collar.ui.domain.settings.models.SettingsParameters
import com.infinum.collar.ui.presentation.shared.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject

@Inject
internal class CollarViewModel(
    private val entityRepository: Repositories.Entity,
    private val settingsRepository: Repositories.Settings
) : BaseViewModel<CollarState, CollarEvent>() {

    companion object {
        private val FILTERS_ALL = listOf(
            EntityType.SCREEN,
            EntityType.EVENT,
            EntityType.PROPERTY
        )
    }

    private var showSystemNotifications: Boolean = false
    private var showInAppNotifications: Boolean = false

    private var parameters: EntityParameters = EntityParameters(
        query = null,
        filters = FILTERS_ALL
    )

    fun search(value: String?) {
        parameters = parameters.copy(query = value)
        entities()
    }

    fun filter(
        screens: Boolean,
        events: Boolean,
        properties: Boolean
    ) {
        var currentFilters = parameters.filters.toSet()
        currentFilters = when (screens) {
            true -> currentFilters.plus(EntityType.SCREEN)
            false -> currentFilters.minus(EntityType.SCREEN)
        }
        currentFilters = when (events) {
            true -> currentFilters.plus(EntityType.EVENT)
            false -> currentFilters.minus(EntityType.EVENT)
        }
        currentFilters = when (properties) {
            true -> currentFilters.plus(EntityType.PROPERTY)
            false -> currentFilters.minus(EntityType.PROPERTY)
        }

        parameters = parameters.copy(filters = currentFilters.toList())
        entities()
    }

    fun notifications(
        enabledSystemNotifications: Boolean,
        enabledInAppNotifications: Boolean
    ) =
        launch {
            io {
                settingsRepository.save(
                    SettingsParameters(
                        entity = SettingsEntity(
                            id = 1,
                            showSystemNotifications = enabledSystemNotifications,
                            showInAppNotifications = enabledInAppNotifications
                        )
                    )
                )
            }
        }

    fun entities() =
        launch {
            entityRepository.load(parameters)
                .flowOn(runningDispatchers)
                .catch { error -> setError(error) }
                .collectLatest {
                    setState(CollarState.Data(entities = it))
                }
        }

    fun entity(id: Long) {
        launch {
            io {
                entityRepository.loadById(
                    EntityParameters(entityId = id)
                )
            }
                .takeIf { it.isNotEmpty() }
                ?.let {
                    emitEvent(CollarEvent.ShowEntity(entity = it.first()))
                }
        }
    }

    fun settings() =
        launch {
            settingsRepository.load(SettingsParameters())
                .flowOn(runningDispatchers)
                .catch { error -> setError(error) }
                .collectLatest {
                    showSystemNotifications = it.showSystemNotifications
                    showInAppNotifications = it.showInAppNotifications
                    emitEvent(
                        CollarEvent.SettingsChanged(
                            analyticsCollectionEnabled = it.analyticsCollectionEnabled,
                            analyticsCollectionTimestamp = it.analyticsCollectionTimestamp
                        )
                    )
                }
        }

    fun filters() {
        launch {
            val result: Triple<Boolean, Boolean, Boolean> = io {
                Triple(
                    first = parameters.filters.contains(EntityType.SCREEN),
                    second = parameters.filters.contains(EntityType.EVENT),
                    third = parameters.filters.contains(EntityType.PROPERTY)
                )
            }
            emitEvent(
                CollarEvent.Filters(
                    screens = result.first,
                    events = result.second,
                    properties = result.third
                )
            )
        }
    }

    fun showSettings() {
        launch {
            emitEvent(
                CollarEvent.Settings(
                    showSystemNotifications = showSystemNotifications,
                    showInAppNotifications = showInAppNotifications
                )
            )
        }
    }

    fun clearEntities() =
        launch {
            io {
                entityRepository.clear()
            }
            emitEvent(CollarEvent.Clear())
        }
}
