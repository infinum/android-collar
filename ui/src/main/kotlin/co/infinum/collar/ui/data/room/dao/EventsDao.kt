package co.infinum.collar.ui.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.infinum.collar.ui.data.room.entity.EventEntity

@Dao
internal interface EventsDao {

    @Insert
    fun save(entity: EventEntity)

    @Query("SELECT * FROM events")
    fun load(): LiveData<List<EventEntity>>
}