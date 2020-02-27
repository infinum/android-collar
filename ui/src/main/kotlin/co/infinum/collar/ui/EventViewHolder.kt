package co.infinum.collar.ui

import android.view.View
import kotlinx.android.synthetic.main.item_event.view.*

class EventViewHolder(
    private val view: View
) : CollarViewHolder<EventEntry>(view) {

    override fun bind(entry: EventEntry) {
        with(view) {
            timeView.text = entry.timestamp.toString()
            iconView.setBackgroundColor(entry.color)
            iconView.setImageResource(entry.icon)
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