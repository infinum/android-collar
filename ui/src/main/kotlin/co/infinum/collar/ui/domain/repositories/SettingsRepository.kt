package co.infinum.collar.ui.domain.repositories

import co.infinum.collar.ui.data.Data
import co.infinum.collar.ui.data.sources.local.dao.SettingsDao
import co.infinum.collar.ui.data.models.local.SettingsEntity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal object SettingsRepository {

    private val settings: SettingsDao = Data.database().collar().settingsDao()

    private val executor: Executor = Executors.newSingleThreadExecutor()

    init {
        save(SettingsEntity())
    }

    fun save(entity: SettingsEntity) =
        executor.execute {
            settings.save(entity)
        }

    fun load() = settings.load()
}
