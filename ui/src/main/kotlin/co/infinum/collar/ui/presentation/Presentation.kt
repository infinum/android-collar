package co.infinum.collar.ui.presentation

import android.content.Context
import android.content.Intent
import co.infinum.collar.ui.domain.Domain
import co.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationProvider
import co.infinum.collar.ui.presentation.notifications.system.SystemNotificationProvider

internal object Presentation {

    private lateinit var systemNotificationProvider: SystemNotificationProvider

    private lateinit var inAppNotificationProvider: InAppNotificationProvider

    private lateinit var launchIntent: Intent

    private lateinit var context: Context

    fun initialise(context: Context) {
        this.context = context
        this.launchIntent = Intent(this.context, CollarActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.systemNotificationProvider = SystemNotificationProvider(context)
        this.inAppNotificationProvider = InAppNotificationProvider(context)

        Domain.initialise(this.context)
    }

    fun systemNotification() = systemNotificationProvider

    fun inAppNotification() = inAppNotificationProvider

    fun launchIntent() = launchIntent

    fun show() = context.startActivity(launchIntent)
}
