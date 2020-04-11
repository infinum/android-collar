package co.infinum.processor.specs

import co.infinum.processor.extensions.applyIf
import co.infinum.processor.models.PropertyHolder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import java.io.File

class UserPropertiesSpec private constructor(
    outputDir: File,
    private val packageName: String,
    private val simpleName: String,
    private val holders: Set<PropertyHolder>
) : CommonSpec(outputDir, packageName, simpleName) {

    companion object {
        private const val DEFAULT_SIMPLE_NAME = "UserProperties"
        private const val FUNCTION_NAME_TRACK_PROPERTY = "trackProperty"
        private const val PARAMETER_NAME_PROPERTY = "property"
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