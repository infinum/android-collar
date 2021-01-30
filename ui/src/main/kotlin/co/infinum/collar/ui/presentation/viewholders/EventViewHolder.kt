package co.infinum.collar.ui.presentation.viewholders

import co.infinum.collar.ui.databinding.CollarItemEventBinding
import co.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder

internal class EventViewHolder(
    viewBinding: CollarItemEventBinding
) : CollarViewHolder(
    viewBinding.root,
    viewBinding.timeView,
    viewBinding.nameView,
    viewBinding.valueView
)
