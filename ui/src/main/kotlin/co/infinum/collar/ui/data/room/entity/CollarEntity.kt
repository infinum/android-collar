package co.infinum.collar.ui.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "entities")
data class CollarEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null,

    @ColumnInfo(name = "type")
    var type: EntityType?,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long?,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "parameters")
    var parameters: String? = null,

    @ColumnInfo(name = "value")
    var value: String? = null
)