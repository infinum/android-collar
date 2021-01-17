package co.infinum.collar.ui.data.sources.local

import android.content.Context
import androidx.room.Room

internal class RoomDatabaseProvider(
    context: Context
) : DatabaseProvider {

    private val database: CollarDatabase =
        Room.inMemoryDatabaseBuilder(context.applicationContext, CollarDatabase::class.java)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    override fun collar() = database
}
