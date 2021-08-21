package com.infinum.collar.ui.presentation

import com.infinum.collar.ui.data.models.local.CollarEntity

internal sealed class CollarState {

    data class Data(
        val entities: List<CollarEntity>
    ) : CollarState()
}
