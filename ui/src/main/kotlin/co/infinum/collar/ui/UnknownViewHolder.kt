package co.infinum.collar.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_unknown.view.*

class UnknownViewHolder(
    private val view: View
) : CollarViewHolder<UnknownEntry>(view) {

    override fun bind(entry: UnknownEntry) {
        with(view) {
            timeView.text = entry.timestamp.toString()
            iconView.setBackgroundColor(entry.color)
            iconView.setImageResource(entry.icon)
            nameView.text = entry.name
        }
    }

    override fun unbind() {
    }
}