package com.infinum.collar.ui.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.infinum.collar.ui.BuildConfig
import com.infinum.collar.ui.domain.Domain
import com.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationFactory
import com.infinum.collar.ui.presentation.notifications.system.SystemNotificationFactory
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
internal object Presentation {

    object Constants {
        const val FORMAT_DATETIME = "dd.MM.yyyy. HH:mm:ss"
        const val FORMAT_ITEM_DATETIME = "HH:mm:ss"

        const val MIME_TYPE_TEXT = "text/plain"

        const val KEY_ENTITY_TYPE = "KEY_ENTITY_TYPE"
        const val KEY_ENTITY_TIMESTAMP = "KEY_ENTITY_TIMESTAMP"
        const val KEY_ENTITY_NAME = "KEY_ENTITY_NAME"
        const val KEY_ENTITY_PARAMETERS = "KEY_ENTITY_PARAMETERS"
        const val KEY_ENTITY_VALUE = "KEY_ENTITY_VALUE"
    }

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private lateinit var context: Context

    private lateinit var launchIntent: Intent

    fun init(context: Context) {
        this.context = context
        this.launchIntent = Intent(this.context, CollarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun launchIntent() =
        if (this::context.isInitialized) {
            launchIntent
        } else {
            throw NullPointerException("Presentation context has not been initialized.")
        }

    fun show() =
        if (this::context.isInitialized) {
            context.startActivity(launchIntent)
        } else {
            throw NullPointerException("Presentation context has not been initialized.")
        }

    fun modules(): List<Module> =
        Domain.modules().plus(
            listOf(
                notifications(),
                viewModels()
            )
        )

    private fun notifications() = module {
        single { SystemNotificationFactory(get()) }
        single { InAppNotificationFactory(get()) }
    }

    private fun viewModels() = module {
        viewModel { CollarViewModel(get(), get()) }
    }
}
