package co.infinum.genlib.generators

import co.infinum.genlib.extensions.toCamelCase
import co.infinum.genlib.models.DataType
import co.infinum.genlib.models.ListType
import co.infinum.genlib.models.Property
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import java.util.Locale

class GeneratorUtils private constructor() {

    companion object {

        fun getClassName(property: Property): TypeName {
            val dataTypeEnum = DataType.getFromKey(property.type)
            val listTypeEnum = ListType.getFromKey(property.listType)

            if (property.values?.isNotEmpty() == true) {
                return if (dataTypeEnum == DataType.LIST) {
                    ClassName("kotlin.collections", "List")
                        .parameterizedBy(ClassName("", getParameterEnumName(property.name)))
                } else {
                    ClassName("", getParameterEnumName(property.name))
                }
            }

            return when (dataTypeEnum) {
                DataType.TEXT -> ClassName("kotlin", "String")
                DataType.NUMBER -> ClassName("kotlin", "Int")
                DataType.DECIMAL -> ClassName("kotlin", "Double")
                DataType.BOOL -> ClassName("kotlin", "Boolean")
                DataType.LIST -> {
                    val listClassName = ClassName("kotlin.collections", "List")
                    when (listTypeEnum) {
                        ListType.TEXT -> {
                            listClassName.parameterizedBy(ClassName("kotlin", "String"))
                        }
                        ListType.NUMBER -> {
                            return listClassName.parameterizedBy(ClassName("kotlin", "Int"))
                        }
                        ListType.DECIMAL -> {
                            listClassName.parameterizedBy(ClassName("kotlin", "Double"))
                        }
                        ListType.BOOL -> {
                            listClassName.parameterizedBy(ClassName("kotlin", "Boolean"))
                        }
                        ListType.UNKNOWN -> throw Exception("$listTypeEnum is not supported")
                    }
                }
                DataType.UNKNOWN -> throw Exception("$dataTypeEnum is not supported")
            }
        }

        fun getParameterName(parameter: String) = parameter.toCamelCase().decapitalize()

        fun getParameterEnumName(parameter: String) = parameter.toCamelCase() + "Enum"

        fun getParameterValueEnumName(value: String): String {
            return value.toUpperCase(Locale.ENGLISH).replace(" ", "_").replace(".","_")
        }
    }
}