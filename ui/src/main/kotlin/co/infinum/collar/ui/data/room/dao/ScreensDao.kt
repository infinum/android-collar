package co.infinum.collar.ui.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.infinum.collar.ui.data.room.entity.ScreenEntity

@Dao
internal interface ScreensDao {

    @Insert
    fun save(entity: ScreenEntity)

    @Query("SELECT * FROM screens")
    fun load(): LiveData<List<ScreenEntity>>
}