package co.infinum.collar.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class CollarViewHolder<in Entry>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(entry: Entry)

    abstract fun unbind()
}