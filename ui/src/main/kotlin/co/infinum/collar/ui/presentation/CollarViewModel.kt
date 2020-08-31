package co.infinum.collar.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.domain.repositories.EntityRepository
import co.infinum.collar.ui.domain.repositories.SettingsRepository

internal class CollarViewModel : ViewModel() {

    companion object {
        private val FILTERS_ALL = listOf(EntityType.SCREEN, EntityType.EVENT, EntityType.PROPERTY)
    }

    private val searchQuery = MutableLiveData<String?>()

    private val filters = MutableLiveData<List<EntityType>>()

    init {
        search(null)
        setFilters(FILTERS_ALL)
    }

    fun entities(): LiveData<List<CollarEntity>> = searchEntities()

    fun search(value: String?) {
        searchQuery.value = value
    }

    fun filter(entityType: EntityType, checked: Boolean) {
        val currentFilters = filters.value.orEmpty().toMutableList()
        when (checked) {
            true -> currentFilters.add(entityType)
            false -> currentFilters.remove(entityType)
        }
        setFilters(currentFilters.toList())
    }

    fun settings() = SettingsRepository.load()

    fun notifications(enabledSystemNotifications: Boolean, enabledInAppNotifications: Boolean) =
        SettingsRepository.save(
            SettingsEntity(
                id = 1,
                showSystemNotifications = enabledSystemNotifications,
                showInAppNotifications = enabledInAppNotifications
            )
        )

    fun clear() = EntityRepository.clearAll()

    private fun setFilters(value: List<EntityType>) {
        filters.value = value
    }

    private fun searchEntities(): LiveData<List<CollarEntity>> =
        Transformations.switchMap(this.searchQuery) { query ->
            when {
                query.isNullOrBlank() -> filterEntities()
                else -> EntityRepository.load(query)
            }
        }

    private fun filterEntities(): LiveData<List<CollarEntity>> =
        Transformations.switchMap(this.filters) { typeFilters ->
            if (typeFilters == FILTERS_ALL) {
                EntityRepository.loadAll()
            } else {
                EntityRepository.load(typeFilters)
            }
        }
}