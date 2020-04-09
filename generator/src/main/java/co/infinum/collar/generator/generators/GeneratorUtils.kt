package co.infinum.collar.generator.generators

import co.infinum.collar.generator.extensions.hasDigit
import co.infinum.collar.generator.extensions.toCamelCase
import co.infinum.collar.generator.models.DataType
import co.infinum.collar.generator.models.Parameter
import co.infinum.collar.generator.models.Property
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import java.util.*

class GeneratorUtils private constructor() {

    companion object {

        fun getClassName(parameter: Parameter): TypeName {
            val dataTypeEnum = parameter.type

            if (parameter.values?.isNotEmpty() == true) {
                return ClassName("kotlin", "String")
            }

            return when (dataTypeEnum) {
                DataType.TEXT.toString() -> ClassName("kotlin", "String")
                DataType.NUMBER.toString() -> ClassName("kotlin", "Long")
                DataType.DECIMAL.toString() -> ClassName("kotlin", "Double")
                DataType.BOOLEAN.toString() -> ClassName("kotlin", "Boolean")
                else -> throw Exception("$dataTypeEnum is not supported")
            }
        }

        fun getClassName(property: Property): TypeName {
            return ClassName("kotlin", "String")
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