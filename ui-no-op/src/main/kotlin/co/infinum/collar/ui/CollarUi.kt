package co.infinum.collar.ui

import android.content.Intent

/**
 * No operation singleton object for UI entry point with convenience methods
 */
object CollarUi {

    /**
     * No operation stub that creates an empty Intent.
     */
    @JvmStatic
    fun launchIntent() = Intent()

    /**
     * No operation stub that does nothing.
     */
    @JvmStatic
    fun show() = Unit
}
