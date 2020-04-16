package co.infinum.collar.ui.notifications.inapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.app.ShareCompat
import co.infinum.collar.ui.R
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.notifications.inapp.snackbar.CollarSnackbar
import co.infinum.collar.ui.notifications.NotificationProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InAppNotificationProvider(
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
            R.drawable.collar_shape_background_screen,
            R.drawable.collar_ic_screen,
            entity
        )
    }

    override fun showEvent(entity: CollarEntity) {
        buildNotification(
            callbacks.currentActivity,
            R.drawable.collar_shape_background_event,
            R.drawable.collar_ic_event,
            entity
        )
    }

    override fun showProperty(entity: CollarEntity) {
        buildNotification(
            callbacks.currentActivity,
            R.drawable.collar_shape_background_property,
            R.drawable.collar_ic_property,
            entity
        )
    }

    private fun buildNotification(
        activity: Activity?,
        @DrawableRes background: Int,
        @DrawableRes icon: Int,
        entity: CollarEntity
    ) {
        CollarSnackbar.make(
            activity?.findViewById(android.R.id.content),
            background,
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
