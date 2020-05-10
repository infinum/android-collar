package co.infinum.collar.ui.data

import android.content.Context
import co.infinum.collar.ui.data.sources.local.DatabaseProvider

internal object Data {

    private lateinit var databaseProvider: DatabaseProvider

    fun initialise(context: Context) {
        databaseProvider = DatabaseProvider.initialise(context)
    }

    fun database() = databaseProvider
}
