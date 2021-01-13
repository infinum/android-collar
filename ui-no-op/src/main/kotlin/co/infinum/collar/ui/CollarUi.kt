package co.infinum.collar.ui

import android.content.Intent

/**
 * No operation singleton object for UI entry point with convenience methods
 */
public object CollarUi {

    /**
     * No operation stub that creates an empty Intent.
     */
    @JvmStatic
    public fun launchIntent(): Intent = Intent()

    /**
     * No operation stub that does nothing.
     */
    @JvmStatic
    public fun show(): Unit = Unit
}
