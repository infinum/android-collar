package co.infinum.collar.ui.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import co.infinum.collar.ui.databinding.CollarItemEventBinding
import co.infinum.collar.ui.databinding.CollarItemPropertyBinding
import co.infinum.collar.ui.databinding.CollarItemScreenBinding
import co.infinum.collar.ui.presentation.viewholders.EventViewHolder
import co.infinum.collar.ui.presentation.viewholders.PropertyViewHolder
import co.infinum.collar.ui.presentation.viewholders.ScreenViewHolder
import co.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder
import java.util.concurrent.TimeUnit
import kotlin.math.abs

internal class CollarAdapter(
    private val onClick: (CollarEntity) -> Unit
) : ListAdapter<CollarEntity, CollarViewHolder>(CollarDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_UNKNOWN = 0
        private const val VIEW_TYPE_SCREEN = 1
        private const val VIEW_TYPE_EVENT = 2
        private const val VIEW_TYPE_PROPERTY = 3
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollarViewHolder =
        LayoutInflater.from(parent.context).run {
            when (viewType) {
                VIEW_TYPE_SCREEN -> ScreenViewHolder(CollarItemScreenBinding.inflate(
                    this,
                    parent,
                    false
                ))
                VIEW_TYPE_EVENT -> EventViewHolder(CollarItemEventBinding.inflate(
                    this,
                    parent,
                    false
                ))
                VIEW_TYPE_PROPERTY -> PropertyViewHolder(CollarItemPropertyBinding.inflate(
                    this,
                    parent,
                    false
                ))
                else -> throw NotImplementedError()
            }
        }

    override fun onBindViewHolder(holder: CollarViewHolder, position: Int) =
        getItem(position).let {
            when (it.type) {
                EntityType.SCREEN -> (holder as ScreenViewHolder).bind(
                    it,
                    shouldShowTimestamp(position),
                    onClick
                )
                EntityType.EVENT -> (holder as EventViewHolder).bind(
                    it,
                    shouldShowTimestamp(position),
                    onClick
                )
                EntityType.PROPERTY -> (holder as PropertyViewHolder).bind(
                    it,
                    shouldShowTimestamp(position),
                    onClick
                )
                else -> Unit
            }
        }

    override fun onViewRecycled(holder: CollarViewHolder) =
        when (holder) {
            is ScreenViewHolder -> holder.unbind()
            is EventViewHolder -> holder.unbind()
            is PropertyViewHolder -> holder.unbind()
            else -> super.onViewRecycled(holder)
        }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position).type) {
            EntityType.SCREEN -> VIEW_TYPE_SCREEN
            EntityType.EVENT -> VIEW_TYPE_EVENT
            EntityType.PROPERTY -> VIEW_TYPE_PROPERTY
            else -> VIEW_TYPE_UNKNOWN
        }

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    fun clear() {
        submitList(listOf())
    }

    private fun shouldShowTimestamp(position: Int): Boolean =
        when (position) {
            0 -> true
            else -> {
                val currentItem = getItem(position)
                val previousItem = getItem(position - 1)
                abs(currentItem.timestamp - previousItem.timestamp) >=
                    TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)
            }
        }
}
