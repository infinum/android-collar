package co.infinum.collar.ui.presentation

import androidx.recyclerview.widget.DiffUtil
import co.infinum.collar.ui.data.models.local.CollarEntity

internal class CollarDiffCallback : DiffUtil.ItemCallback<CollarEntity>() {

    override fun areItemsTheSame(oldItem: CollarEntity, newItem: CollarEntity): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CollarEntity, newItem: CollarEntity): Boolean =
        oldItem == newItem
}
