package co.infinum.collar.ui.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null,

    @ColumnInfo(name = "timestamp")
    override var timestamp: Long?,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "parameters")
    var parameters: String?
) : CollarEntity