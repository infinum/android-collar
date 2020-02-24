package co.infinum.processor.specs

import co.infinum.processor.models.PropertyHolder
import co.infinum.processor.models.PropertyParameterHolder
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File

class UserPropertiesSpec private constructor(
    outputDir: File,
    private val packageName: String,
    private val simpleName: String,
    private val holders: Map<PropertyHolder, List<PropertyParameterHolder>>
) : Spec {

    companion object {
        private val CLASS_COLLAR = ClassName("co.infinum.collar", "Collar")

        private const val FUNCTION_NAME_TRACK_PROPERTY = "trackProperty"

        private const val PARAMETER_NAME_PROPERTY = "property"
        private const val PARAMETER_NAME_PROPERTY_NAME = "name"
        private const val PARAMETER_NAME_PROPERTY_VALUE = "value"

        private const val DEFAULT_PACKAGE_NAME = ""
        private const val DEFAULT_SIMPLE_NAME = "UserProperties"
    }

    open class Builder(
        private var outputDir: File? = null,
        private var className: ClassName = ClassName(DEFAULT_PACKAGE_NAME, DEFAULT_SIMPLE_NAME),
        private var holders: Map<PropertyHolder, List<PropertyParameterHolder>> = mapOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun className(className: ClassName) = apply { this.className = className }
        fun holders(holders: Map<PropertyHolder, List<PropertyParameterHolder>>) = apply { this.holders = holders }
        fun build() = UserPropertiesSpec(outputDir!!, className.packageName, className.simpleName, holders)
    }

    init {
        build().writeTo(outputDir)
    }

    override fun file(): FileSpec =
        FileSpec.builder(packageName, simpleName)
            .addAnnotation(jvmName())
            .addComment(comment().toString())
            .build()

    override fun jvmName(): AnnotationSpec =
        AnnotationSpec.builder(JvmName::class.java)
            .addMember("%S", "${CLASS_COLLAR.simpleName}$simpleName")
            .build()

    override fun comment(): CodeBlock =
        CodeBlock.builder()
            .addStatement("Converts [%T] to property name and value and logs it using [%T.%L].", ClassName(packageName, simpleName), CLASS_COLLAR, FUNCTION_NAME_TRACK_PROPERTY)
            .addStatement("")
            .addStatement("This is a generated extension file. Do not edit.")
            .build()

    override fun build(): FileSpec =
        file().toBuilder().apply {
            val extensionFunSpecBuilder = FunSpec.builder(FUNCTION_NAME_TRACK_PROPERTY)
                .addParameter(PARAMETER_NAME_PROPERTY, ClassName(packageName, simpleName))
                .addStatement("val %L: %T", PARAMETER_NAME_PROPERTY_NAME, String::class)
                .addStatement("val %L: %T", PARAMETER_NAME_PROPERTY_VALUE, String::class)
                .beginControlFlow("when (%L)", PARAMETER_NAME_PROPERTY)
            for ((declaredUserProperty, propertyParamList) in holders) {
                val codeBlock = CodeBlock.builder()
                    .addStatement("is %T -> {", declaredUserProperty.className)
                    .indent()
                    .addStatement(
                        "%L = %S",
                        PARAMETER_NAME_PROPERTY_NAME,
                        declaredUserProperty.resolvedName
                    )
                    .addStatement(
                        "%L = %L.%L",
                        PARAMETER_NAME_PROPERTY_VALUE,
                        PARAMETER_NAME_PROPERTY,
                        propertyParamList.first().variableName
                    )
                    .unindent()
                    .addStatement("}")
                    .build()

                extensionFunSpecBuilder.addCode(codeBlock)
            }
            extensionFunSpecBuilder.endControlFlow()
                .addStatement(
                    "%T.%L(%L, %L)",
                    CLASS_COLLAR,
                    FUNCTION_NAME_TRACK_PROPERTY,
                    PARAMETER_NAME_PROPERTY_NAME,
                    PARAMETER_NAME_PROPERTY_VALUE
                )

            addFunction(extensionFunSpecBuilder.build())
        }.build()
}

@DslMarker
annotation class UserPropertiesSpecDsl

@UserPropertiesSpecDsl
class UserPropertiesSpecBuilder : UserPropertiesSpec.Builder()

inline fun userPropertiesSpec(builder: UserPropertiesSpecBuilder.() -> Unit): UserPropertiesSpec {
    val specBuilder = UserPropertiesSpecBuilder()
    specBuilder.builder()
    return specBuilder.build()
}