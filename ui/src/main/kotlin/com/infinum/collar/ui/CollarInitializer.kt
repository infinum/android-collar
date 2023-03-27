package com.infinum.collar.ui

import android.content.Context
import androidx.startup.Initializer
import com.infinum.collar.ui.di.LibraryComponents

internal class CollarInitializer : Initializer<Class<CollarInitializer>> {

    override fun create(context: Context): Class<CollarInitializer> {

        LibraryComponents.initialize(context)

        return CollarInitializer::class.java
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        mutableListOf()
}
