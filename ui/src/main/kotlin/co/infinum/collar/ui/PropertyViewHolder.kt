package co.infinum.collar.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_property.view.*

class PropertyViewHolder(
    private val view: View
) : CollarViewHolder<PropertyEntry>(view) {

    override fun bind(entry: PropertyEntry) {
        with(view) {
            timeView.text = entry.timestamp.toString()
            iconView.setBackgroundColor(entry.color)
            iconView.setImageResource(entry.icon)
            nameView.text = entry.name
            valueView.text = entry.value
        }
    }

    override fun unbind() {
    }
}