package com.infinum.collar.ui.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.infinum.collar.ui.data.models.local.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: SettingsEntity): Long

    @Query("SELECT * FROM settings WHERE id=1 LIMIT 1")
    fun load(): Flow<SettingsEntity>
}
