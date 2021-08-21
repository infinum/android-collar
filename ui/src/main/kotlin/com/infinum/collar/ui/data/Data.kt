package com.infinum.collar.ui.data

import androidx.room.Room
import com.infinum.collar.ui.data.sources.local.CollarDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal object Data {

    fun modules(): List<Module> =
        listOf(
            local()
        )

    private fun local() = module {
        single {
            Room.inMemoryDatabaseBuilder(get(), CollarDatabase::class.java)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
        single { get<CollarDatabase>().entities() }
        single { get<CollarDatabase>().settings() }
    }
}
