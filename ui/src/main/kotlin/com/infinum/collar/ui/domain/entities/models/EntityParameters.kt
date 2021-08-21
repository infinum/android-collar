package com.infinum.collar.ui.domain.entities.models

import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.domain.shared.base.BaseParameters

internal data class EntityParameters(
    val query: String? = null,
    val filters: List<EntityType> = listOf(),
    val entity: CollarEntity? = null
) : BaseParameters
