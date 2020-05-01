package co.infinum.collar.ui

import android.content.Context
import android.content.Intent

object CollarUi {

    @JvmStatic
    fun launchIntent(context: Context) =
        Intent(context, CollarActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
