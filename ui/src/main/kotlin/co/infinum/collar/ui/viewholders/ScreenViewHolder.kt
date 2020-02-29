package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.data.room.entity.CollarEntity
import kotlinx.android.synthetic.main.item_screen.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenViewHolder(
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
        }
    }

    override fun unbind() {
        with(view) {
            timeView.visibility = View.INVISIBLE
        }
    }
}