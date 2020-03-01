package co.infinum.collar.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import co.infinum.collar.ui.viewholders.EventViewHolder
import co.infinum.collar.ui.viewholders.PropertyViewHolder
import co.infinum.collar.ui.viewholders.ScreenViewHolder
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class CollarAdapter(
    private var items: List<CollarEntity> = listOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_UNKNOWN = 0
        private const val VIEW_TYPE_SCREEN = 1
        private const val VIEW_TYPE_EVENT = 2
        private const val VIEW_TYPE_PROPERTY = 3
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        LayoutInflater.from(parent.context).run {
            when (viewType) {
                VIEW_TYPE_SCREEN -> ScreenViewHolder(this.inflate(R.layout.item_screen, parent, false))
                VIEW_TYPE_EVENT -> EventViewHolder(this.inflate(R.layout.item_event, parent, false))
                VIEW_TYPE_PROPERTY -> PropertyViewHolder(this.inflate(R.layout.item_property, parent, false))
                else -> throw NotImplementedError()
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (items[position].type) {
            EntityType.SCREEN -> (holder as ScreenViewHolder).bind(items[position], shouldShowTimestamp(position))
            EntityType.EVENT -> (holder as EventViewHolder).bind(items[position], shouldShowTimestamp(position))
            EntityType.PROPERTY -> (holder as PropertyViewHolder).bind(items[position], shouldShowTimestamp(position))
            else -> Unit
        }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) =
        when (holder) {
            is ScreenViewHolder -> holder.unbind()
            is EventViewHolder -> holder.unbind()
            is PropertyViewHolder -> holder.unbind()
            else -> super.onViewRecycled(holder)
        }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        when (items[position].type) {
            EntityType.SCREEN -> VIEW_TYPE_SCREEN
            EntityType.EVENT -> VIEW_TYPE_EVENT
            EntityType.PROPERTY -> VIEW_TYPE_PROPERTY
            else -> VIEW_TYPE_UNKNOWN
        }

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    fun addItems(newItems: List<CollarEntity>) {
        val diffCallback = CollarDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = listOf()
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun clear() {
        items = listOf()
        notifyDataSetChanged()
    }

    private fun shouldShowTimestamp(position: Int): Boolean =
        when (position) {
            0 -> true
            else -> {
                val currentItem = items[position]
                val previousItem = items[position - 1]
                abs((currentItem.timestamp ?: 0) - (previousItem.timestamp ?: 0)) >= TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)
            }
        }
}