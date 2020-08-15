package co.infinum.collar.ui.presentation.notifications.inapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.ShareCompat
import co.infinum.collar.ui.R
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.presentation.notifications.inapp.snackbar.CollarSnackbar
import co.infinum.collar.ui.presentation.notifications.NotificationProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class InAppNotificationProvider(
    context: Context
) : NotificationProvider {

    companion object {
        const val FORMAT_DATETIME = "HH:mm:ss"
        const val SHARE_TYPE = "text/plain"
        const val LINE_SEPARATOR = "\n"
    }

    private val callbacks = CollarActivityLifecycleCallbacks()

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(callbacks)
    }

    override fun showScreen(entity: CollarEntity) {
        buildNotification(
            callbacks.currentActivity,
            R.color.collar_color_screen,
            R.drawable.collar_ic_screen_white,
            entity
        )
    }

    override fun showEvent(entity: CollarEntity) {
        buildNotification(
            callbacks.currentActivity,
            R.color.collar_color_event,
            R.drawable.collar_ic_event_white,
            entity
        )
    }

    override fun showProperty(entity: CollarEntity) {
        buildNotification(
            callbacks.currentActivity,
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
            SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(entity.timestamp)),
            View.OnClickListener {
                activity?.let {
                    it.startActivity(
                        ShareCompat.IntentBuilder.from(it)
                            .setType(SHARE_TYPE)
                            .setText(listOfNotNull(
                                entity.name,
                                entity.parameters
                            ).joinToString(LINE_SEPARATOR))
                            .createChooserIntent()
                    )
                }
            }
        ).show()
    }
}
