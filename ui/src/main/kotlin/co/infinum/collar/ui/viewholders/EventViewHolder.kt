package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.data.room.entity.CollarEntity
import kotlinx.android.synthetic.main.collar_item_event.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventViewHolder(
    private val view: View
) : CollarViewHolder(view) {

    override fun bind(entry: CollarEntity, showTimestamp: Boolean) {
        with(view) {
            if (showTimestamp) {
                timeView.visibility = View.VISIBLE
            } else {
                timeView.visibility = View.INVISIBLE
            }
            timeView.text = entry.timestamp?.let { SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(it)) }
            nameView.text = entry.name
            valueView.text = entry.parameters?.let { it } ?: "-"
        }
    }

    override fun unbind() {
        with(view) {
            timeView.visibility = View.INVISIBLE
        }
    }
}