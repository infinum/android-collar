package co.infinum.collar.ui.presentation.viewholders

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.databinding.CollarItemScreenBinding
import co.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class ScreenViewHolder(
    private val viewBinding: CollarItemScreenBinding
) : CollarViewHolder(viewBinding.root) {

    override fun bind(entity: CollarEntity, showTimestamp: Boolean, onClick: (CollarEntity) -> Unit) {
        with(viewBinding) {
            if (showTimestamp) {
                timeView.isVisible = true
            } else {
                timeView.isInvisible = true
            }
            timeView.text = SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(entity.timestamp))
            nameView.text = entity.name
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
