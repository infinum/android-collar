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

    override fun bind(entity: CollarEntity, showTimestamp: Boolean, onClick: (CollarEntity) -> Unit) {
        with(view) {
            if (showTimestamp) {
                timeView.visibility = View.VISIBLE
            } else {
                timeView.visibility = View.INVISIBLE
            }
            timeView.text = entity.timestamp?.let { SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(it)) }
            nameView.text = entity.name
            valueView.text = entity.parameters?.let { it }
            rootLayout.setOnClickListener { onClick(entity) }
        }
    }

    override fun unbind() {
        with(view) {
            rootLayout.setOnClickListener(null)
            timeView.visibility = View.INVISIBLE
        }
    }
}