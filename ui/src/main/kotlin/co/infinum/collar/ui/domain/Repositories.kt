package co.infinum.collar.ui.domain

import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.domain.entities.models.EntityParameters
import co.infinum.collar.ui.domain.settings.models.SettingsParameters
import co.infinum.collar.ui.domain.shared.base.BaseRepository

internal interface Repositories {

    interface Entity : BaseRepository<EntityParameters, List<CollarEntity>> {

        suspend fun clear()
    }

    interface Settings : BaseRepository<SettingsParameters, SettingsEntity>
}
