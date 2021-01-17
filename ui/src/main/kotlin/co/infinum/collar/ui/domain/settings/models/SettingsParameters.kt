package co.infinum.collar.ui.domain.settings.models

import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.domain.shared.base.BaseParameters

internal data class SettingsParameters(
    val entity: SettingsEntity = SettingsEntity()
) : BaseParameters