package com.infinum.collar.ui

import android.content.Context
import androidx.startup.Initializer
import com.infinum.collar.ui.di.LibraryKoin
import com.infinum.collar.ui.presentation.Presentation

internal class CollarInitializer : Initializer<Class<CollarInitializer>> {

    override fun create(context: Context): Class<CollarInitializer> {

        LibraryKoin.init(context)

        Presentation.init(context)

        return CollarInitializer::class.java
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        mutableListOf()
}
