package com.infinum.collar.ui.data.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
internal data class SettingsEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Long = 1,

    @ColumnInfo(name = "analyticsCollection")
    var analyticsCollectionEnabled: Boolean = false,

    @ColumnInfo(name = "analyticsCollectionTimestamp")
    var analyticsCollectionTimestamp: Long = 0L,

    @ColumnInfo(name = "showSystemNotifications")
    var showSystemNotifications: Boolean = false,

    @ColumnInfo(name = "showInAppNotifications")
    var showInAppNotifications: Boolean = false
)
