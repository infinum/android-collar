package co.infinum.collar.ui.data.room.repository

import android.content.Context
import androidx.lifecycle.LiveData
import co.infinum.collar.ui.data.room.CollarDatabase
import co.infinum.collar.ui.data.room.dao.EntitiesDao
import co.infinum.collar.ui.data.room.entity.CollarEntity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal object EntityRepository {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private lateinit var entities: EntitiesDao

    fun initialize(context: Context) {
        val database = CollarDatabase.create(context)
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
}