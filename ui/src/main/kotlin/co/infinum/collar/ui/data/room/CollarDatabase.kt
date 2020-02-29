package co.infinum.collar.ui.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.infinum.collar.ui.data.room.dao.EntitiesDao
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.typeconverters.CollarTypeConverter

@Database(entities = [CollarEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [CollarTypeConverter::class])
internal abstract class CollarDatabase : RoomDatabase() {

    companion object {

        fun create(context: Context): CollarDatabase =
            Room.inMemoryDatabaseBuilder(context, CollarDatabase::class.java)
                .fallbackToDestructiveMigration()
                .build()
    }

    abstract fun entitiesDao(): EntitiesDao
}