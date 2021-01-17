package co.infinum.collar.ui.domain.entities.models

import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import co.infinum.collar.ui.domain.shared.base.BaseParameters

internal data class EntityParameters(
    val query: String? = null,
    val filters: List<EntityType> = listOf(),
    val entity: CollarEntity? = null
) : BaseParameters