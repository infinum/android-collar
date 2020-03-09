package co.infinum.generator.generators

import co.infinum.generator.extensions.hasDigits
import co.infinum.generator.extensions.toCamelCase
import co.infinum.generator.models.DataType
import co.infinum.generator.models.ListType
import co.infinum.generator.models.Property
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
                DataType.BOOLEAN -> ClassName("kotlin", "Boolean")
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
                        ListType.BOOLEAN -> {
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
            var parameterValueEnumName = value.toUpperCase(Locale.ENGLISH).replace(" ", "_").replace(".", "_")
            if (value.hasDigits()) {
                parameterValueEnumName = "NUMBER_$parameterValueEnumName"
            }
            return parameterValueEnumName
        }
    }
}