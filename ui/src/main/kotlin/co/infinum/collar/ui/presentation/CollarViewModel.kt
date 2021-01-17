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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        global {
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

    fun clear(action: suspend () -> Unit) =
        global {
            io {
                entityRepository.clear()
            }
            withContext(Dispatchers.Main) {
                action()
            }
        }

    fun entities(onData: suspend (List<CollarEntity>) -> Unit) =
        global {
            io {
                entityRepository.load(parameters)
                    .collectLatest {
                        withContext(Dispatchers.Main) {
                            onData(it)
                        }
                    }
            }
        }

    fun settings(onData: (SettingsEntity) -> Unit) =
        global {
            io {
                settingsRepository.load(
                    SettingsParameters()
                )
                    .collectLatest {
                        withContext(Dispatchers.Main) {
                            onData(it)
                        }
                    }
            }
        }
}
