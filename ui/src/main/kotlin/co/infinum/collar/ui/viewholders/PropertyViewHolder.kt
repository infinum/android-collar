package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.entries.PropertyEntry
import kotlinx.android.synthetic.main.item_property.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PropertyViewHolder(
    private val view: View
) : CollarViewHolder<PropertyEntry>(view) {

    override fun bind(entry: PropertyEntry) {
        with(view) {
            timeView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(entry.timestamp))
            nameView.text = entry.name
            valueView.text = entry.value
        }
    }

    override fun unbind() {
    }
}