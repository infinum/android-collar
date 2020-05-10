package co.infinum.collar.ui.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.infinum.collar.ui.data.sources.local.dao.EntitiesDao
import co.infinum.collar.ui.data.sources.local.dao.SettingsDao
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.data.sources.local.typeconverters.CollarTypeConverter

@Database(entities = [CollarEntity::class, SettingsEntity::class], version = 2, exportSchema = false)
@TypeConverters(value = [CollarTypeConverter::class])
internal abstract class CollarDatabase : RoomDatabase() {

    abstract fun entitiesDao(): EntitiesDao

    abstract fun settingsDao(): SettingsDao
}
