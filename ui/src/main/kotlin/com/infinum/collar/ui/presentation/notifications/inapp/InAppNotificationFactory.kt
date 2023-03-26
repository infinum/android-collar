package com.infinum.collar.ui.presentation.notifications.inapp

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.ShareCompat
import com.infinum.collar.ui.R
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.extensions.presentationItemFormat
import com.infinum.collar.ui.presentation.notifications.NotificationFactory
import com.infinum.collar.ui.presentation.notifications.inapp.snackbar.CollarSnackbar
import com.infinum.collar.ui.presentation.notifications.shared.CollarActivityCallbacks
import com.infinum.collar.ui.presentation.shared.Constants
import java.util.Date
import me.tatarka.inject.annotations.Inject

@Inject
internal class InAppNotificationFactory(
    context: Context,
    private val callbacks: CollarActivityCallbacks
) : NotificationFactory {

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(callbacks)
    }

    override fun showScreen(entity: CollarEntity) {
        buildNotification(
            callbacks.current(),
            R.color.collar_color_screen,
            R.drawable.collar_ic_screen_white,
            entity
        )
    }

    override fun showEvent(entity: CollarEntity) {
        buildNotification(
            callbacks.current(),
            R.color.collar_color_event,
            R.drawable.collar_ic_event_white,
            entity
        )
    }

    override fun showProperty(entity: CollarEntity) {
        buildNotification(
            callbacks.current(),
            R.color.collar_color_property,
            R.drawable.collar_ic_property_white,
            entity
        )
    }

    private fun buildNotification(
        activity: Activity?,
        @ColorRes backgroundTint: Int,
        @DrawableRes icon: Int,
        entity: CollarEntity
    ) {
        CollarSnackbar.make(
            activity?.findViewById(android.R.id.content),
            backgroundTint,
            icon,
            entity.name,
            entity.parameters,
            Date(entity.timestamp).presentationItemFormat
        ) {
            activity?.let {
                it.startActivity(
                    ShareCompat.IntentBuilder(it)
                        .setType(Constants.MIME_TYPE_TEXT)
                        .setText(
                            listOfNotNull(
                                entity.name,
                                entity.parameters
                            ).joinToString(System.lineSeparator())
                        )
                        .createChooserIntent()
                )
            }
        }.show()
    }
}
