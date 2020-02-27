package co.infinum.collar.ui.viewholders

import android.view.View
import co.infinum.collar.ui.entries.UnknownEntry
import kotlinx.android.synthetic.main.item_unknown.view.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UnknownViewHolder(
    private val view: View
) : CollarViewHolder<UnknownEntry>(view) {

    override fun bind(entry: UnknownEntry) {
        with(view) {
            timeView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(entry.timestamp))
            nameView.text = entry.name
        }
    }

    override fun unbind() {
    }
}