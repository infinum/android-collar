package co.infinum.collar.ui.data.sources.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.infinum.collar.ui.data.models.local.SettingsEntity

@Dao
internal interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: SettingsEntity)

    @Query("SELECT * FROM settings WHERE id=1 LIMIT 1")
    fun load(): LiveData<SettingsEntity>
}
