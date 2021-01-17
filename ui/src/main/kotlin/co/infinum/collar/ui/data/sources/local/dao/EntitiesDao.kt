@file:Suppress("MaxLineLength", "MaximumLineLength")

package co.infinum.collar.ui.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import kotlinx.coroutines.flow.Flow

@Dao
internal interface EntitiesDao {

    @Insert
    suspend fun save(entity: CollarEntity)

    @Query("SELECT * FROM entities ORDER BY timestamp DESC")
    fun load(): Flow<List<CollarEntity>>

    @Query("SELECT * FROM entities WHERE name LIKE '%' || :query || '%' OR value LIKE '%' || :query || '%' OR parameters LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun load(query: String): Flow<List<CollarEntity>>

    @Query("SELECT * FROM entities WHERE type IN (:filters) ORDER BY timestamp DESC")
    fun load(filters: List<EntityType>): Flow<List<CollarEntity>>

    @Query("SELECT * FROM entities WHERE (name LIKE '%' || :query || '%' OR value LIKE '%' || :query || '%' OR parameters LIKE '%' || :query || '%') AND type IN (:filters) ORDER BY timestamp DESC")
    fun load(query: String, filters: List<EntityType>): Flow<List<CollarEntity>>

    @Query("DELETE FROM entities")
    suspend fun delete()
}
