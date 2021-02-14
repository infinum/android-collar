package com.infinum.collar.ui.presentation

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.infinum.collar.ui.R
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.databinding.CollarViewDetailBinding
import com.infinum.collar.ui.extensions.presentationFormat
import java.util.Date
import java.util.Locale

internal class CollarDialogFactory(private val activity: Activity) {

    private val inflater = LayoutInflater.from(activity)

    fun entityDetail(entity: CollarEntity): AlertDialog =
        MaterialAlertDialogBuilder(activity)
            .setIcon(resolveDetailIcon(entity))
            .setTitle(resolveDetailTitle(entity))
            .setView(resolveDetailView(entity))
            .setPositiveButton(R.string.collar_share) { _, _ -> share(entity) }
            .create()

    private fun resolveDetailIcon(entity: CollarEntity) =
        when (entity.type) {
            EntityType.SCREEN -> R.drawable.collar_ic_screen
            EntityType.EVENT -> R.drawable.collar_ic_event
            EntityType.PROPERTY -> R.drawable.collar_ic_property
            else -> 0
        }

    private fun resolveDetailTitle(entity: CollarEntity) =
        entity.type?.name?.toLowerCase(Locale.getDefault())?.capitalize(Locale.getDefault())

    private fun resolveDetailView(entity: CollarEntity) =
        with(CollarViewDetailBinding.inflate(inflater)) {
            timeView.text = Date(entity.timestamp).presentationFormat
            nameView.text = entity.name
            valueCaptionView.text = when (entity.type) {
                EntityType.EVENT -> entity.parameters?.let {
                    activity.getString(R.string.collar_parameters)
                }
                EntityType.PROPERTY -> activity.getString(R.string.collar_value)
                else -> null
            }.also {
                if (it.isNullOrBlank()) {
                    valueView.isGone = true
                    valueCaptionView.isGone = true
                } else {
                    valueView.isVisible = true
                    valueCaptionView.isVisible = true
                    valueView.text = when (entity.type) {
                        EntityType.EVENT -> entity.parameters
                        EntityType.PROPERTY -> entity.value
                        else -> null
                    }
                }
            }
            root
        }

    private fun share(entity: CollarEntity) {
        ShareCompat.IntentBuilder.from(activity)
            .setChooserTitle(R.string.collar_name)
            .setType(Presentation.Constants.MIME_TYPE_TEXT)
            .setText(
                listOfNotNull(
                    "time: ${Date(entity.timestamp).presentationFormat}",
                    entity.name?.let { "name: $it" },
                    entity.type?.let { "type: ${it.name.toLowerCase(Locale.getDefault())}" },
                    entity.value?.let { "value: $it" },
                    entity.parameters?.let { "parameters: $it" }
                ).joinToString("\n")
            )
            .startChooser()
    }
}
