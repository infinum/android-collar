package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.entries.ScreenEntry
import kotlinx.android.synthetic.main.item_screen.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenViewHolder(
    private val view: View
) : CollarViewHolder<ScreenEntry>(view) {

    override fun bind(entry: ScreenEntry) {
        with(view) {
            timeView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(entry.timestamp))
            nameView.text = entry.name
        }
    }

    override fun unbind() {
        with(view) {

        }
    }
}