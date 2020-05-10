package co.infinum.collar.ui.domain.repositories

import androidx.lifecycle.LiveData
import co.infinum.collar.ui.data.Data
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal object EntityRepository {

    private val entities = Data.database().collar().entitiesDao()

    private val executor: Executor = Executors.newSingleThreadExecutor()

    fun saveScreen(entity: CollarEntity) =
        executor.execute {
            entities.save(entity)
        }

    fun saveEvent(entity: CollarEntity) =
        executor.execute {
            entities.save(entity)
        }

    fun saveProperty(entity: CollarEntity) =
        executor.execute {
            entities.save(entity)
        }

    fun loadAll() = entities.load()

    fun clearAll() {
        executor.execute {
            entities.delete()
        }
    }

    fun load(query: String): LiveData<List<CollarEntity>> = entities.load(query)

    fun load(filters: List<EntityType>): LiveData<List<CollarEntity>> = entities.load(filters)
}
