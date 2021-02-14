package com.infinum.collar.ui.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.databinding.CollarItemEventBinding
import com.infinum.collar.ui.databinding.CollarItemPropertyBinding
import com.infinum.collar.ui.databinding.CollarItemScreenBinding
import com.infinum.collar.ui.extensions.inflater
import com.infinum.collar.ui.presentation.viewholders.EventViewHolder
import com.infinum.collar.ui.presentation.viewholders.PropertyViewHolder
import com.infinum.collar.ui.presentation.viewholders.ScreenViewHolder
import com.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder
import java.util.concurrent.TimeUnit
import kotlin.math.abs

internal class CollarAdapter(
    private val onListChanged: (Boolean) -> Unit,
    private val onClick: (CollarEntity) -> Unit
) : ListAdapter<CollarEntity, CollarViewHolder>(CollarDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_UNKNOWN = 0
        private const val VIEW_TYPE_SCREEN = 1
        private const val VIEW_TYPE_EVENT = 2
        private const val VIEW_TYPE_PROPERTY = 3

        private val TIME_SPAN = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollarViewHolder =
        when (viewType) {
            VIEW_TYPE_SCREEN -> ScreenViewHolder(
                CollarItemScreenBinding.inflate(
                    parent.inflater,
                    parent,
                    false
                )
            )
            VIEW_TYPE_EVENT -> EventViewHolder(
                CollarItemEventBinding.inflate(
                    parent.inflater,
                    parent,
                    false
                )
            )
            VIEW_TYPE_PROPERTY -> PropertyViewHolder(
                CollarItemPropertyBinding.inflate(
                    parent.inflater,
                    parent,
                    false
                )
            )
            else -> throw NotImplementedError()
        }

    override fun onBindViewHolder(holder: CollarViewHolder, position: Int) =
        with(getItem(position)) {
            when (type) {
                EntityType.SCREEN -> (holder as ScreenViewHolder).bind(
                    this,
                    isTimestampVisible(position),
                    onClick
                )
                EntityType.EVENT -> (holder as EventViewHolder).bind(
                    this,
                    isTimestampVisible(position),
                    onClick
                )
                EntityType.PROPERTY -> (holder as PropertyViewHolder).bind(
                    this,
                    isTimestampVisible(position),
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

    override fun onCurrentListChanged(
        previousList: MutableList<CollarEntity>,
        currentList: MutableList<CollarEntity>
    ) = onListChanged(currentList.isEmpty())

    /**
     * Always show formatted timestamp on first item.
     * For other items show formatted timestamp only on first item in the same time span.
     */
    private fun isTimestampVisible(position: Int): Boolean =
        when (position) {
            0 -> true
            else -> abs(getItem(position).timestamp - getItem(position - 1).timestamp) >= TIME_SPAN
        }
}
