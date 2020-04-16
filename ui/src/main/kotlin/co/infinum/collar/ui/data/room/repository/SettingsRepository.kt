package co.infinum.collar.ui.data.room.repository

import co.infinum.collar.ui.data.room.CollarDatabase
import co.infinum.collar.ui.data.room.dao.SettingsDao
import co.infinum.collar.ui.data.room.entity.SettingsEntity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal object SettingsRepository {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private lateinit var settings: SettingsDao

    fun initialize(database: CollarDatabase) {
        settings = database.settingsDao()
    }

    fun save(entity: SettingsEntity) =
        executor.execute {
            settings.save(entity)
        }

    fun load() = settings.load()
}
