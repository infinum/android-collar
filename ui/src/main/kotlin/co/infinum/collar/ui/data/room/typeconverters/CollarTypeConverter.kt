package co.infinum.collar.ui.data.room.typeconverters

import androidx.room.TypeConverter
import co.infinum.collar.ui.data.room.entity.EntityType

class CollarTypeConverter {

    @TypeConverter
    fun toEnum(name: String): EntityType = EntityType.valueOf(name)

    @TypeConverter
    fun fromEnum(type: EntityType): String = type.name
}
