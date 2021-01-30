package co.infinum.collar.ui.presentation.viewholders.shared

import android.view.View
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.extensions.presentationItemFormat
import java.util.Date

internal abstract class CollarViewHolder(
    private val rootView: View,
    private val timeView: TextView,
    private val nameView: TextView,
    private val valueView: TextView? = null
) : RecyclerView.ViewHolder(rootView) {

    fun bind(entity: CollarEntity, showTimestamp: Boolean, onClick: (CollarEntity) -> Unit) {
        if (showTimestamp) {
            timeView.isVisible = true
        } else {
            timeView.isInvisible = true
        }
        timeView.text = Date(entity.timestamp).presentationItemFormat
        nameView.text = entity.name
        valueView?.text = entity.value
        rootView.setOnClickListener { onClick(entity) }
    }

    fun unbind() {
        rootView.setOnClickListener(null)
        timeView.isInvisible = true
    }
}
