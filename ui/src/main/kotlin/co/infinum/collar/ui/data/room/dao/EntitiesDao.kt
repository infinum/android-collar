package co.infinum.collar.ui.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.infinum.collar.ui.data.room.entity.CollarEntity

@Dao
internal interface EntitiesDao {

    @Insert
    fun save(entity: CollarEntity)

    @Query("SELECT * FROM entities ORDER BY timestamp DESC")
    fun load(): LiveData<List<CollarEntity>>

    @Query("SELECT * FROM entities WHERE name LIKE '%' || :query || '%' OR value LIKE '%' || :query || '%' OR parameters LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun load(query: String): LiveData<List<CollarEntity>>

    @Query("DELETE FROM entities")
    fun delete()
}