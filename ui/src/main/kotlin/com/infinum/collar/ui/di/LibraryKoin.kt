package com.infinum.collar.ui.di

import android.content.Context
import com.infinum.collar.ui.BuildConfig
import com.infinum.collar.ui.presentation.Presentation
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.logger.Level

internal object LibraryKoin {

    private val koinApplication = KoinApplication.init()

    fun koin(): Koin = koinApplication.koin

    fun init(context: Context) {
        koinApplication.apply {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(context)
            modules(Presentation.modules())
        }
    }
}
