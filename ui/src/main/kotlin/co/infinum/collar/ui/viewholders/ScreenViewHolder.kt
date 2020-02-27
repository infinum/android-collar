package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.data.room.entity.ScreenEntity
import kotlinx.android.synthetic.main.item_screen.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenViewHolder(
    private val view: View
) : CollarViewHolder<ScreenEntity>(view) {

    override fun bind(entry: ScreenEntity) {
        with(view) {
            timeView.text = entry.timestamp?.let { SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(it)) }
            nameView.text = entry.name
        }
    }

    override fun unbind() {
        with(view) {

        }
    }
}