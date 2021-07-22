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

    private var parameters: EntityParameters = EntityParameters(
        query = null,
        filters = FILTERS_ALL
    )

    fun search(value: String?) {
        parameters = parameters.copy(query = value)
        entities()
    }

    fun filter(
        entityType: EntityType,
        checked: Boolean
    ) {
        val currentFilters = parameters.filters.toMutableList()
        when (checked) {
            true -> currentFilters.add(entityType)
            false -> currentFilters.remove(entityType)
        }
        parameters = parameters.copy(filters = currentFilters.toList())
        entities()
    }

    fun notifications(enabledSystemNotifications: Boolean, enabledInAppNotifications: Boolean) =
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

    fun clearEntities() =
        launch {
            io {
                entityRepository.clear()
            }
            emitEvent(CollarEvent.Clear())
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

    fun settings() =
        launch {
            settingsRepository.load(SettingsParameters())
                .flowOn(runningDispatchers)
                .catch { error -> setError(error) }
                .collectLatest {
                    emitEvent(CollarEvent.Settings(entity = it))
                }
        }
}
