package co.infinum.collar.ui.domain.settings

import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.data.sources.local.DatabaseProvider
import co.infinum.collar.ui.domain.Repositories
import co.infinum.collar.ui.domain.settings.models.SettingsParameters
import kotlinx.coroutines.flow.Flow

internal class SettingsRepository(
    private val database: DatabaseProvider
) : Repositories.Settings {

    override suspend fun save(input: SettingsParameters) =
        database.collar().settings().save(input.entity)

    override suspend fun load(input: SettingsParameters): Flow<SettingsEntity> =
        database.collar().settings().load()
}
