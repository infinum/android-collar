package com.infinum.collar.ui.domain.settings

import com.infinum.collar.ui.data.models.local.SettingsEntity
import com.infinum.collar.ui.data.sources.local.dao.SettingsDao
import com.infinum.collar.ui.domain.Repositories
import com.infinum.collar.ui.domain.settings.models.SettingsParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

internal class SettingsRepository(
    private val dao: SettingsDao
) : Repositories.Settings {

    init {
        runBlocking {
            dao.save(SettingsEntity())
        }
    }

    override suspend fun save(input: SettingsParameters) =
        dao.save(input.entity)

    override suspend fun load(input: SettingsParameters): Flow<SettingsEntity> =
        dao.load()
}
