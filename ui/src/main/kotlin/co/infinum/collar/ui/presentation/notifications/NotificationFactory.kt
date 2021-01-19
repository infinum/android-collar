package co.infinum.collar.ui.presentation.notifications

import co.infinum.collar.ui.data.models.local.CollarEntity

internal interface NotificationFactory {

    fun showScreen(entity: CollarEntity)

    fun showEvent(entity: CollarEntity)

    fun showProperty(entity: CollarEntity)
}
