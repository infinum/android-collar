package com.infinum.collar.ui.di.component

import android.content.Context
import androidx.room.Room
import com.infinum.collar.ui.data.sources.local.CollarDatabase
import com.infinum.collar.ui.data.sources.local.dao.EntitiesDao
import com.infinum.collar.ui.data.sources.local.dao.SettingsDao
import com.infinum.collar.ui.di.scope.DataScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides

@Component
@DataScope
internal abstract class DataComponent(
    private val context: Context
) {

    abstract val database: CollarDatabase

    abstract val entitiesDao: EntitiesDao

    abstract val settingsDao: SettingsDao

    @Provides
    @Inject
    @DataScope
    fun database(): CollarDatabase =
        Room.inMemoryDatabaseBuilder(context, CollarDatabase::class.java)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @DataScope
    fun entities(): EntitiesDao =
        database.entities()

    @Provides
    @DataScope
    fun settings(): SettingsDao =
        database.settings()
}
