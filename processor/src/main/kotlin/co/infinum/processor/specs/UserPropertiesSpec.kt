package co.infinum.processor.specs

import co.infinum.processor.extensions.applyIf
import co.infinum.processor.models.PropertyHolder
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File

class UserPropertiesSpec private constructor(
    private val outputDir: File,
    private val packageName: String,
    private val simpleName: String,
    private val holders: Set<PropertyHolder>
) : Spec {

    companion object {
        private val CLASS_COLLAR = ClassName("co.infinum.collar", "Collar")

        private const val FUNCTION_NAME_TRACK_PROPERTY = "trackProperty"

        private const val PARAMETER_NAME_PROPERTY = "property"

        private const val DEFAULT_PACKAGE_NAME = "co.infinum.collar"
        private const val DEFAULT_SIMPLE_NAME = "UserProperties"
    }

    open class Builder(
        private var outputDir: File? = null,
        private var className: ClassName = ClassName(DEFAULT_PACKAGE_NAME, DEFAULT_SIMPLE_NAME),
        private var holders: Set<PropertyHolder> = setOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun className(className: ClassName) = apply { this.className = className }
        fun holders(holders: Set<PropertyHolder>) = apply { this.holders = holders }
        fun build() = UserPropertiesSpec(outputDir!!, className.packageName, className.simpleName, holders)
    }

    init {
        build()
    }

    override fun file(): FileSpec =
        FileSpec.builder(packageName, simpleName)
            .addAnnotation(jvmName())
            .addComment(comment().toString())
            .apply { extensions().map { addFunction(it) } }
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

    override fun extensions(): List<FunSpec> =
        listOf(
            FunSpec.builder(FUNCTION_NAME_TRACK_PROPERTY)
                .addParameter(PARAMETER_NAME_PROPERTY, ClassName(packageName, simpleName))
                .applyIf(holders.isNotEmpty()) {
                    beginControlFlow("when (%L)", PARAMETER_NAME_PROPERTY)
                    addCode(
                        CodeBlock.builder()
                            .apply {
                                holders.forEach {
                                    addStatement("is %T -> %T.%L(%S, %L.%L)", it.className, CLASS_COLLAR, FUNCTION_NAME_TRACK_PROPERTY, it.propertyName, PARAMETER_NAME_PROPERTY, it.propertyParameterNames.single())
                                }
                            }
                            .build()
                    )
                    endControlFlow()
                }
                .build()
        )

    override fun build() =
        file().writeTo(outputDir)
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