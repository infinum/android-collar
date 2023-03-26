package com.infinum.collar.ui.di.component

import com.infinum.collar.ui.data.sources.local.dao.EntitiesDao
import com.infinum.collar.ui.data.sources.local.dao.SettingsDao
import com.infinum.collar.ui.di.scope.DomainScope
import com.infinum.collar.ui.domain.Repositories
import com.infinum.collar.ui.domain.entities.EntityRepository
import com.infinum.collar.ui.domain.settings.SettingsRepository
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@DomainScope
internal abstract class DomainComponent(
    @Component val dataComponent: DataComponent
) {

    abstract val entities: Repositories.Entity

    abstract val settings: Repositories.Settings

    @Provides
    @DomainScope
    fun entities(dao: EntitiesDao): Repositories.Entity =
        EntityRepository(dao)

    @Provides
    @DomainScope
    fun settings(dao: SettingsDao): Repositories.Settings =
        SettingsRepository(dao)
}
