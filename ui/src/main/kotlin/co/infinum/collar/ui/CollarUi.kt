package co.infinum.collar.ui

import co.infinum.collar.ui.presentation.Presentation

/**
 * Singleton object for UI entry point with convenience methods
 */
object CollarUi {

    /**
     * Creates and prepares an Intent for launching UI.
     * This intent starts CollarActivity and applies Intent.FLAG_ACTIVITY_NEW_TASK flag.
     */
    @JvmStatic
    fun launchIntent() = Presentation.launchIntent()

    /**
     * Directly shows CollarActivity by previously prepared Intent.
     * This intent applies Intent.FLAG_ACTIVITY_NEW_TASK flag.
     */
    @JvmStatic
    fun show() = Presentation.show()
}
