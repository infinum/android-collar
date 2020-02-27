package co.infinum.collar.ui.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null,

    @ColumnInfo(name = "timestamp")
    override var timestamp: Long?,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "value")
    var value: String?
) : CollarEntity