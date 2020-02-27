package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.data.room.entity.EventEntity
import kotlinx.android.synthetic.main.item_event.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventViewHolder(
    private val view: View
) : CollarViewHolder<EventEntity>(view) {

    override fun bind(entry: EventEntity) {
        with(view) {
            timeView.text = entry.timestamp?.let { SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(it)) }
            nameView.text = entry.name
            valueView.text = entry.parameters
        }
    }

    override fun unbind() {
    }
}