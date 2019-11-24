package co.infinum.collar

import android.app.Activity

/**
 * This is the container model for the triggered screen tracking.
 */
data class Screen(
    val activity: Activity,
    val name: String
)