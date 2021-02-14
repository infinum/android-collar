package com.infinum.collar.ui.domain

import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.SettingsEntity
import com.infinum.collar.ui.domain.entities.models.EntityParameters
import com.infinum.collar.ui.domain.settings.models.SettingsParameters
import com.infinum.collar.ui.domain.shared.base.BaseRepository

internal interface Repositories {

    interface Entity : BaseRepository<EntityParameters, List<CollarEntity>> {

        suspend fun clear()
    }

    interface Settings : BaseRepository<SettingsParameters, SettingsEntity>
}
