package co.infinum.collar.ui.data.sources.local.typeconverters

import androidx.room.TypeConverter
import co.infinum.collar.ui.data.models.local.EntityType

internal class CollarTypeConverter {

    @TypeConverter
    fun toEnum(name: String): EntityType = EntityType.valueOf(name)

    @TypeConverter
    fun fromEnum(type: EntityType): String = type.name
}
