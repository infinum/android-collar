package co.infinum.collar.ui.notifications

import co.infinum.collar.ui.data.room.entity.CollarEntity

interface NotificationProvider {

    fun showScreen(entity: CollarEntity)

    fun showEvent(entity: CollarEntity)

    fun showProperty(entity: CollarEntity)
}
