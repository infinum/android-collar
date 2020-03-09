package co.infinum.generator.generators

import co.infinum.generator.extensions.hasDigit
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
            val dataTypeEnum = property.type
            val listTypeEnum = property.listType

            if (property.values?.isNotEmpty() == true) {
                return if (dataTypeEnum == DataType.LIST.toString()) {
                    ClassName("kotlin.collections", "List")
                        .parameterizedBy(ClassName("", getParameterEnumName(property.name)))
                } else {
                    ClassName("", getParameterEnumName(property.name))
                }
            }

            return when (dataTypeEnum) {
                DataType.TEXT.toString() -> ClassName("kotlin", "String")
                DataType.NUMBER.toString() -> ClassName("kotlin", "Int")
                DataType.DECIMAL.toString() -> ClassName("kotlin", "Double")
                DataType.BOOLEAN.toString() -> ClassName("kotlin", "Boolean")
                DataType.LIST.toString() -> {
                    val listClassName = ClassName("kotlin.collections", "List")
                    when (listTypeEnum) {
                        ListType.TEXT.toString() -> {
                            listClassName.parameterizedBy(ClassName("kotlin", "String"))
                        }
                        ListType.NUMBER.toString() -> {
                            return listClassName.parameterizedBy(ClassName("kotlin", "Int"))
                        }
                        ListType.DECIMAL.toString() -> {
                            listClassName.parameterizedBy(ClassName("kotlin", "Double"))
                        }
                        ListType.BOOLEAN.toString() -> {
                            listClassName.parameterizedBy(ClassName("kotlin", "Boolean"))
                        }
                        else -> throw Exception("$listTypeEnum is not supported")
                    }
                }
                else -> throw Exception("$dataTypeEnum is not supported")
            }
        }

        fun getParameterName(parameter: String) = parameter.toCamelCase().decapitalize()

        fun getParameterEnumName(parameter: String) = parameter.toCamelCase() + "Enum"

        fun getParameterValueEnumName(value: String): String {
            var parameterValueEnumName = value.toUpperCase(Locale.ENGLISH).replace(" ", "_").replace(".", "_")
            if (value.hasDigit()) {
                parameterValueEnumName = "NUMBER_$parameterValueEnumName"
            }
            return parameterValueEnumName
        }
    }
}