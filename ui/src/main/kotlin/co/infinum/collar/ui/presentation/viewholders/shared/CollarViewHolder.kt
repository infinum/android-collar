package co.infinum.collar.ui.presentation.viewholders.shared

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.data.models.local.CollarEntity

internal abstract class CollarViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(
        entity: CollarEntity,
        showTimestamp: Boolean,
        onClick: (CollarEntity) -> Unit
    )

    abstract fun unbind()
}
