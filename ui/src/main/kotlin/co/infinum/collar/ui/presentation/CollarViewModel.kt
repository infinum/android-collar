package co.infinum.collar.ui.presentation

import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.domain.Repositories
import co.infinum.collar.ui.domain.entities.models.EntityParameters
import co.infinum.collar.ui.domain.settings.models.SettingsParameters
import co.infinum.collar.ui.presentation.shared.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn

internal class CollarViewModel(
    private val entityRepository: Repositories.Entity,
    private val settingsRepository: Repositories.Settings
) : BaseViewModel() {

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

    fun search(
        value: String?,
        onData: suspend (List<CollarEntity>) -> Unit
    ) {
        parameters = parameters.copy(query = value)
        entities(onData)
    }

    fun filter(
        entityType: EntityType,
        checked: Boolean,
        onData: suspend (List<CollarEntity>) -> Unit
    ) {
        val currentFilters = parameters.filters.toMutableList()
        when (checked) {
            true -> currentFilters.add(entityType)
            false -> currentFilters.remove(entityType)
        }
        parameters = parameters.copy(filters = currentFilters.toList())
        entities(onData)
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

    fun clearEntities(action: suspend () -> Unit) =
        launch {
            io {
                entityRepository.clear()
            }
            action()
        }

    fun entities(onData: suspend (List<CollarEntity>) -> Unit) =
        launch {
            entityRepository.load(parameters)
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    onData(it)
                }
        }

    fun settings(onData: (SettingsEntity) -> Unit) =
        launch {
            settingsRepository.load(SettingsParameters())
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    onData(it)
                }
        }
}
