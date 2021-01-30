package co.infinum.collar.ui.presentation.viewholders

import co.infinum.collar.ui.databinding.CollarItemScreenBinding
import co.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder

internal class ScreenViewHolder(
    viewBinding: CollarItemScreenBinding
) : CollarViewHolder(
    viewBinding.root,
    viewBinding.timeView,
    viewBinding.nameView
)
