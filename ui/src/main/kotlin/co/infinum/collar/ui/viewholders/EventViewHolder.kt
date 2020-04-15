package co.infinum.collar.ui.viewholders

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.databinding.CollarItemEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventViewHolder(
    private val viewBinding: CollarItemEventBinding
) : CollarViewHolder(viewBinding.root) {

    override fun bind(entity: CollarEntity, showTimestamp: Boolean, onClick: (CollarEntity) -> Unit) {
        with(viewBinding) {
            if (showTimestamp) {
                timeView.isVisible = true
            } else {
                timeView.isInvisible = true
            }
            timeView.text = entity.timestamp?.let { SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(it)) }
            nameView.text = entity.name
            valueView.text = entity.parameters?.let { it }
            root.setOnClickListener { onClick(entity) }
        }
    }

    override fun unbind() {
        with(viewBinding) {
            root.setOnClickListener(null)
            timeView.isInvisible = true
        }
    }
}