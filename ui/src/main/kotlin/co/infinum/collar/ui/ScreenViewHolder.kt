package co.infinum.collar.ui

import android.view.View
import kotlinx.android.synthetic.main.item_screen.view.*

class ScreenViewHolder(
    private val view: View
) : CollarViewHolder<ScreenEntry>(view) {

    override fun bind(entry: ScreenEntry) {
        with(view) {
            timeView.text = entry.timestamp.toString()
            iconView.setBackgroundColor(entry.color)
            iconView.setImageResource(entry.icon)
            nameView.text = entry.name
        }
    }

    override fun unbind() {
        with(view) {

        }
    }
}