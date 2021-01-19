package co.infinum.collar.ui.presentation

import android.content.Context
import android.content.Intent
import co.infinum.collar.ui.BuildConfig
import co.infinum.collar.ui.domain.Domain
import co.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationFactory
import co.infinum.collar.ui.presentation.notifications.system.SystemNotificationFactory
import co.infinum.collar.ui.presentation.shared.logger.Stump
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

internal object Presentation {

    object Constants {
        const val FORMAT_DATETIME = "dd.MM.yyyy. HH:mm:ss"
        const val FORMAT_ITEM_DATETIME = "HH:mm:ss"

        const val MIME_TYPE_TEXT = "text/plain"
    }

    private lateinit var systemNotificationFactory: SystemNotificationFactory

    private lateinit var inAppNotificationFactory: InAppNotificationFactory

    private lateinit var launchIntent: Intent

    private lateinit var context: Context

    fun init(context: Context) {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            false -> Timber.plant(Stump())
        }
        this.context = context
        this.launchIntent = Intent(this.context, CollarActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.systemNotificationFactory = SystemNotificationFactory(context)
        this.inAppNotificationFactory = InAppNotificationFactory(context)
    }

    fun systemNotificationFactory() = systemNotificationFactory

    fun inAppNotificationFactory() = inAppNotificationFactory

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
