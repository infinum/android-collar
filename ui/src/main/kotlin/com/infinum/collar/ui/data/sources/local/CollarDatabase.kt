package com.infinum.collar.ui.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.SettingsEntity
import com.infinum.collar.ui.data.sources.local.dao.EntitiesDao
import com.infinum.collar.ui.data.sources.local.dao.SettingsDao
import com.infinum.collar.ui.data.sources.local.typeconverters.CollarTypeConverter

@Database(
    entities = [
        CollarEntity::class,
        SettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(value = [CollarTypeConverter::class])
internal abstract class CollarDatabase : RoomDatabase() {

    abstract fun entities(): EntitiesDao

    abstract fun settings(): SettingsDao
}
