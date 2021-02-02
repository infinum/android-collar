package co.infinum.collar.ui.presentation.notifications.inapp

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.ShareCompat
import co.infinum.collar.ui.R
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.extensions.presentationItemFormat
import co.infinum.collar.ui.presentation.Presentation
import co.infinum.collar.ui.presentation.notifications.NotificationFactory
import co.infinum.collar.ui.presentation.notifications.inapp.snackbar.CollarSnackbar
import java.util.Date

internal class InAppNotificationFactory(
    context: Context
) : NotificationFactory {

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
            Date(entity.timestamp).presentationItemFormat
        ) {
            activity?.let {
                it.startActivity(
                    ShareCompat.IntentBuilder.from(it)
                        .setType(Presentation.Constants.MIME_TYPE_TEXT)
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
