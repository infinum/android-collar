package co.infinum.collar.ui.data.sources.local

import android.content.Context
import androidx.room.Room

internal object DatabaseProvider {

    private lateinit var collarDatabase: CollarDatabase

    fun initialise(context: Context): DatabaseProvider {
        collarDatabase = Room.inMemoryDatabaseBuilder(context.applicationContext, CollarDatabase::class.java)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        return this
    }

    fun collar() = lazyOf(collarDatabase).value
}
