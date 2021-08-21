package com.infinum.collar.ui.presentation.viewholders

import com.infinum.collar.ui.databinding.CollarItemEventBinding
import com.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder

internal class EventViewHolder(
    viewBinding: CollarItemEventBinding
) : CollarViewHolder(
    viewBinding.root,
    viewBinding.iconView,
    viewBinding.timeView,
    viewBinding.nameView,
    viewBinding.valueView
)
