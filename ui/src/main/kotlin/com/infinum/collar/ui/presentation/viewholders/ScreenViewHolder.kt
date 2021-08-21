package com.infinum.collar.ui.presentation.viewholders

import com.infinum.collar.ui.databinding.CollarItemScreenBinding
import com.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder

internal class ScreenViewHolder(
    viewBinding: CollarItemScreenBinding
) : CollarViewHolder(
    viewBinding.root,
    viewBinding.iconView,
    viewBinding.timeView,
    viewBinding.nameView
)
