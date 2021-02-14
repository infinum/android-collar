package com.infinum.collar.ui.domain.settings.models

import com.infinum.collar.ui.data.models.local.SettingsEntity
import com.infinum.collar.ui.domain.shared.base.BaseParameters

internal data class SettingsParameters(
    val entity: SettingsEntity = SettingsEntity()
) : BaseParameters
