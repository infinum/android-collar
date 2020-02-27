package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.entries.EventEntry
import kotlinx.android.synthetic.main.item_event.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventViewHolder(
    private val view: View
) : CollarViewHolder<EventEntry>(view) {

    override fun bind(entry: EventEntry) {
        with(view) {
            timeView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(entry.timestamp))
            nameView.text = entry.name
            valueView.text = entry.parameters.run {
                val map = mutableMapOf<String, String>()

                val ks: Set<String> = this.keySet()
                val iterator = ks.iterator()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    if (this.getString(key).isNullOrBlank().not()) {
                        map[key] = this.getString(key).orEmpty()
                    }
                }
                map.toList().joinToString("\n") { "${it.first} = ${it.second}" }
            }
        }
    }

    override fun unbind() {
    }
}