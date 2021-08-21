package com.infinum.collar.ui.presentation.viewholders.shared

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.infinum.collar.ui.R
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.extensions.presentationItemFormat
import java.util.Date

internal abstract class CollarViewHolder(
    private val rootView: View,
    private val iconView: ImageView,
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            iconView.background.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(
                        iconView.context,
                        when (entity.type) {
                            EntityType.SCREEN -> R.color.collar_color_screen
                            EntityType.EVENT -> R.color.collar_color_event
                            EntityType.PROPERTY -> R.color.collar_color_property
                            null -> android.R.color.white
                        }
                    ),
                    PorterDuff.Mode.SRC_ATOP
                )
            iconView.drawable.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(iconView.context, android.R.color.white),
                    PorterDuff.Mode.SRC_ATOP
                )
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
