package com.infinum.collar.ui.presentation

import com.infinum.collar.ui.data.models.local.SettingsEntity

internal sealed class CollarEvent {

    class Clear : CollarEvent()

    data class Settings(
        val entity: SettingsEntity
    ) : CollarEvent()
}
