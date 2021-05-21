package com.infinum.collar.ui.presentation.viewholders

import com.infinum.collar.ui.databinding.CollarItemPropertyBinding
import com.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder

internal class PropertyViewHolder(
    viewBinding: CollarItemPropertyBinding
) : CollarViewHolder(
    viewBinding.root,
    viewBinding.iconView,
    viewBinding.timeView,
    viewBinding.nameView,
    viewBinding.valueView
)
