package co.infinum.collar.ui.presentation.viewholders

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.databinding.CollarItemPropertyBinding
import co.infinum.collar.ui.extensions.presentationItemFormat
import co.infinum.collar.ui.presentation.viewholders.shared.CollarViewHolder
import java.util.Date

internal class PropertyViewHolder(
    private val viewBinding: CollarItemPropertyBinding
) : CollarViewHolder(viewBinding.root) {

    override fun bind(entity: CollarEntity, showTimestamp: Boolean, onClick: (CollarEntity) -> Unit) =
        with(viewBinding) {
            if (showTimestamp) {
                timeView.isVisible = true
            } else {
                timeView.isInvisible = true
            }
            timeView.text = Date(entity.timestamp).presentationItemFormat
            nameView.text = entity.name
            valueView.text = entity.value
            root.setOnClickListener { onClick(entity) }
        }

    override fun unbind() =
        with(viewBinding) {
            root.setOnClickListener(null)
            timeView.isInvisible = true
        }
}
