package com.infinum.collar.ui.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

internal interface LibraryKoinComponent : KoinComponent {

    override fun getKoin(): Koin =
        LibraryKoin.koin()
}
