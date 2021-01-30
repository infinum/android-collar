package co.infinum.collar.ui.presentation.viewholders

import co.infinum.collar.ui.databinding.CollarItemPropertyBinding
import co.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder

internal class PropertyViewHolder(
    viewBinding: CollarItemPropertyBinding
) : CollarViewHolder(
    viewBinding.root,
    viewBinding.timeView,
    viewBinding.nameView,
    viewBinding.valueView
)
