package co.infinum.collar.ui

import androidx.recyclerview.widget.DiffUtil
import co.infinum.collar.ui.data.room.entity.CollarEntity

internal class CollarDiffCallback(
    private val oldList: List<CollarEntity>,
    private val newList: List<CollarEntity>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
