package co.infinum.collar.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.entries.CollarEntry
import co.infinum.collar.ui.entries.EventEntry
import co.infinum.collar.ui.entries.PropertyEntry
import co.infinum.collar.ui.entries.ScreenEntry
import co.infinum.collar.ui.entries.UnknownEntry
import co.infinum.collar.ui.viewholders.EventViewHolder
import co.infinum.collar.ui.viewholders.PropertyViewHolder
import co.infinum.collar.ui.viewholders.ScreenViewHolder
import co.infinum.collar.ui.viewholders.UnknownViewHolder

class CollarAdapter(
    private var items: List<CollarEntry> = listOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_UNKNOWN = 0
        private const val VIEW_TYPE_SCREEN = 1
        private const val VIEW_TYPE_EVENT = 2
        private const val VIEW_TYPE_PROPERTY = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        LayoutInflater.from(parent.context).run {
            when (viewType) {
                VIEW_TYPE_SCREEN -> ScreenViewHolder(this.inflate(R.layout.item_screen, parent, false))
                VIEW_TYPE_EVENT -> EventViewHolder(this.inflate(R.layout.item_event, parent, false))
                VIEW_TYPE_PROPERTY -> PropertyViewHolder(this.inflate(R.layout.item_property, parent, false))
                else -> UnknownViewHolder(this.inflate(R.layout.item_unknown, parent, false))
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when {
            items[position] is ScreenEntry -> (holder as ScreenViewHolder).bind(items[position] as ScreenEntry)
            items[position] is EventEntry -> (holder as EventViewHolder).bind(items[position] as EventEntry)
            items[position] is PropertyEntry -> (holder as PropertyViewHolder).bind(items[position] as PropertyEntry)
            else -> (holder as UnknownViewHolder).bind(items[position] as UnknownEntry)
        }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) =
        when (holder) {
            is ScreenViewHolder -> holder.unbind()
            is EventViewHolder -> holder.unbind()
            is PropertyViewHolder -> holder.unbind()
            is UnknownViewHolder -> holder.unbind()
            else -> super.onViewRecycled(holder)
        }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        when {
            items[position] is ScreenEntry -> VIEW_TYPE_SCREEN
            items[position] is EventEntry -> VIEW_TYPE_EVENT
            items[position] is PropertyEntry -> VIEW_TYPE_PROPERTY
            else -> VIEW_TYPE_UNKNOWN
        }

    fun addItems(newItems: MutableList<CollarEntry>) {
        items = newItems.plus(items).sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }
}