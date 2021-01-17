package co.infinum.collar.ui.data

import co.infinum.collar.ui.data.sources.local.DatabaseProvider
import co.infinum.collar.ui.data.sources.local.RoomDatabaseProvider
import org.koin.core.module.Module
import org.koin.dsl.module

internal object Data {

    fun modules(): List<Module> =
        listOf(
            local()
        )

    private fun local() = module {
        single<DatabaseProvider> { RoomDatabaseProvider(get()) }
    }
}
