package co.infinum.collar.ui.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.infinum.collar.ui.data.models.local.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: SettingsEntity)

    @Query("SELECT * FROM settings WHERE id=1 LIMIT 1")
    fun load(): Flow<SettingsEntity>
}
