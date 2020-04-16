package co.infinum.collar.ui.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "settings")
data class SettingsEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Long = 1,

    @ColumnInfo(name = "showSystemNotifications")
    var showSystemNotifications: Boolean = true,

    @ColumnInfo(name = "showInAppNotifications")
    var showInAppNotifications: Boolean = true
)