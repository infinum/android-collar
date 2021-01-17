package co.infinum.collar.ui.presentation

import android.content.Context
import android.content.Intent
import co.infinum.collar.ui.BuildConfig
import co.infinum.collar.ui.domain.Domain
import co.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationProvider
import co.infinum.collar.ui.presentation.notifications.system.SystemNotificationProvider
import co.infinum.collar.ui.presentation.shared.logger.Stump
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

internal object Presentation {

    private lateinit var systemNotificationProvider: SystemNotificationProvider

    private lateinit var inAppNotificationProvider: InAppNotificationProvider

    private lateinit var launchIntent: Intent

    private lateinit var context: Context

    fun init(context: Context) {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            false -> Timber.plant(Stump())
        }
        this.context = context
        this.launchIntent = Intent(this.context, CollarActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.systemNotificationProvider = SystemNotificationProvider(context)
        this.inAppNotificationProvider = InAppNotificationProvider(context)
    }

    fun systemNotification() = systemNotificationProvider

    fun inAppNotification() = inAppNotificationProvider

    fun launchIntent() = launchIntent

    fun show() =
        if (this::context.isInitialized) {
            context.startActivity(launchIntent)
        } else {
            throw NullPointerException("Presentation context has not been initialized.")
        }

    fun modules(): List<Module> =
        Domain.modules().plus(
            listOf(
                viewModels()
            )
        )

    private fun viewModels() = module {
        viewModel { CollarViewModel(get(), get()) }
    }
}
