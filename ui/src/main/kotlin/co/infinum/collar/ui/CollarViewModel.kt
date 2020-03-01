package co.infinum.collar.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import co.infinum.collar.ui.data.room.repository.EntityRepository

class CollarViewModel : ViewModel() {

    private val searchQuery = MutableLiveData<String?>()

    init {
        setSearch(null)
    }

    fun setSearch(value: String?) {
        searchQuery.value = value
    }

    fun entities(filters: List<EntityType> = listOf()): LiveData<List<CollarEntity>> {
        return Transformations.switchMap(this.searchQuery) { query ->
            if (query.isNullOrBlank()) {
                EntityRepository.loadAll()
            } else {
                EntityRepository.load(query)
            }
        }
    }

    fun clearAll() = EntityRepository.clearAll()
}