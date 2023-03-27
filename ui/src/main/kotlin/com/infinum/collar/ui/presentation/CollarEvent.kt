package com.infinum.collar.ui.presentation

import com.infinum.collar.ui.data.models.local.CollarEntity

internal sealed class CollarEvent {

    data class ShowEntity(
        val entity: CollarEntity
    ) : CollarEvent()

    data class Filters(
        val screens: Boolean,
        val events: Boolean,
        val properties: Boolean
    ) : CollarEvent()

    data class SettingsChanged(
        val analyticsCollectionEnabled: Boolean,
        val analyticsCollectionTimestamp: Long
    ) : CollarEvent()

    class Clear : CollarEvent()

    data class Settings(
        val showSystemNotifications: Boolean,
        val showInAppNotifications: Boolean
    ) : CollarEvent()
}
