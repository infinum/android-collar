package co.infinum.collar.ui.domain

import co.infinum.collar.ui.data.Data
import co.infinum.collar.ui.domain.entities.EntityRepository
import co.infinum.collar.ui.domain.settings.SettingsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

internal object Domain {

    fun modules(): List<Module> =
        Data.modules().plus(
            listOf(
                entities(),
                settings()
            )
        )

    private fun entities() = module {
        single<Repositories.Entity> { EntityRepository(get()) }
    }

    private fun settings() = module {
        single<Repositories.Settings> { SettingsRepository(get()) }
    }
}
