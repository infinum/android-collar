package com.infinum.collar.ui

import android.content.Intent
import com.infinum.collar.ui.di.LibraryComponents

/**
 * Singleton object for UI entry point with convenience methods
 */
public object CollarUi {

    /**
     * Creates and prepares an Intent for launching UI.
     * This intent starts CollarActivity and applies Intent.FLAG_ACTIVITY_NEW_TASK flag.
     */
    @JvmStatic
    public fun launchIntent(): Intent = LibraryComponents.presentation().launchIntent()

    /**
     * Directly shows CollarActivity by previously prepared Intent.
     * This intent applies Intent.FLAG_ACTIVITY_NEW_TASK flag.
     */
    @JvmStatic
    public fun show(): Unit = LibraryComponents.presentation().show()
}
