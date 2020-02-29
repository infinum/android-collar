package co.infinum.collar.ui.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.infinum.collar.ui.data.room.entity.PropertyEntity

@Dao
internal interface PropertiesDao {

    @Insert
    fun save(entity: PropertyEntity)

    @Query("SELECT * FROM properties")
    fun load(): LiveData<List<PropertyEntity>>

    @Query("DELETE FROM properties")
    fun delete()
}