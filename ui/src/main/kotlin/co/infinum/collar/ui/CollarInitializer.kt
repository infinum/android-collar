package co.infinum.collar.ui

import android.content.Context
import androidx.startup.Initializer
import co.infinum.collar.ui.presentation.Presentation

public class CollarInitializer : Initializer<Class<CollarInitializer>> {

    override fun create(context: Context): Class<CollarInitializer> {
        Presentation.initialise(context)
        return CollarInitializer::class.java
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf()
}
