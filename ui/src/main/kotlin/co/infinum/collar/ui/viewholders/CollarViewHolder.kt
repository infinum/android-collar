package co.infinum.collar.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.data.room.entity.CollarEntity

abstract class CollarViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    companion object {
        const val FORMAT_DATETIME = "HH:mm:ss"
    }

    abstract fun bind(entry: CollarEntity, showTimestamp: Boolean)

    abstract fun unbind()
}