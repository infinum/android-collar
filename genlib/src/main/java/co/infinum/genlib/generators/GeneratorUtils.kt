package co.infinum.genlib.generators

import co.infinum.genlib.models.DataType
import co.infinum.genlib.models.ListType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

class GeneratorUtils private constructor() {

    companion object {

        fun getClassNameFromTypeAndListType(type: String?, listType: String?): TypeName {
            val dataTypeEnum = DataType.getFromKey(type)
            val listTypeEnum = ListType.getFromKey(listType)

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
    }
}