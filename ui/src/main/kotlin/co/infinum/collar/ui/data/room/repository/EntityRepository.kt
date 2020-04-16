package co.infinum.collar.ui.data.room.repository

import androidx.lifecycle.LiveData
import co.infinum.collar.ui.data.room.CollarDatabase
import co.infinum.collar.ui.data.room.dao.EntitiesDao
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal object EntityRepository {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private lateinit var entities: EntitiesDao

    fun initialize(database: CollarDatabase) {
        entities = database.entitiesDao()
    }

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