package co.infinum.collar.ui.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.infinum.collar.ui.data.room.dao.EventsDao
import co.infinum.collar.ui.data.room.dao.PropertiesDao
import co.infinum.collar.ui.data.room.dao.ScreensDao
import co.infinum.collar.ui.data.room.entity.EventEntity
import co.infinum.collar.ui.data.room.entity.PropertyEntity
import co.infinum.collar.ui.data.room.entity.ScreenEntity

@Database(entities = [
    ScreenEntity::class,
    EventEntity::class,
    PropertyEntity::class
], version = 1, exportSchema = false)
internal abstract class CollarDatabase : RoomDatabase() {

    companion object {

        fun create(context: Context): CollarDatabase =
            Room.inMemoryDatabaseBuilder(context, CollarDatabase::class.java)
                .fallbackToDestructiveMigration()
                .build()
    }

    abstract fun screensDao(): ScreensDao

    abstract fun eventsDao(): EventsDao

    abstract fun propertiesDao(): PropertiesDao
}