package co.infinum.collar.processor.specs

import co.infinum.collar.processor.extensions.applyIf
import co.infinum.collar.processor.models.PropertyHolder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import java.io.File

internal class UserPropertiesSpec private constructor(
    outputDir: File,
    private val packageName: String,
    private val simpleName: String,
    private val holders: Set<PropertyHolder>
) : CommonSpec(outputDir, packageName, simpleName) {

    companion object {
        private const val SIMPLE_NAME = "UserProperties"
        private const val FUNCTION_TRACK_PROPERTY = "trackProperty"
        private const val STATEMENT = "is %T -> %T.%L(%S, %L.%L)"
    }

    internal open class Builder(
        private var outputDir: File? = null,
        private var className: ClassName = ClassName(PACKAGE_NAME, SIMPLE_NAME),
        private var holders: Set<PropertyHolder> = setOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun className(className: ClassName) = apply { this.className = className }
        fun holders(holders: Set<PropertyHolder>) = apply { this.holders = holders }
        fun build() = UserPropertiesSpec(outputDir!!, className.packageName, className.simpleName, holders)
    }

    override fun extensions(): List<FunSpec> =
        listOf(
            FunSpec.builder(FUNCTION_TRACK_PROPERTY)
                .addParameter(parameterName(), ClassName(packageName, simpleName))
                .applyIf(holders.isNotEmpty()) {
                    beginControlFlow(CONTROL_FLOW_WHEN, parameterName())
                    addCode(properties())
                    endControlFlow()
                }
                .build()
        )

    private fun properties(): CodeBlock =
        CodeBlock.builder()
            .apply {
                holders.forEach {
                    addStatement(
                        STATEMENT,
                        it.className,
                        CLASS_COLLAR,
                        FUNCTION_TRACK_PROPERTY,
                        it.propertyName,
                        parameterName(),
                        it.propertyParameterNames.single()
                    )
                }
            }
            .build()
}

@DslMarker
internal annotation class UserPropertiesSpecDsl

@UserPropertiesSpecDsl
internal class UserPropertiesSpecBuilder : UserPropertiesSpec.Builder()

internal inline fun userPropertiesSpec(builder: UserPropertiesSpecBuilder.() -> Unit): UserPropertiesSpec {
    val specBuilder = UserPropertiesSpecBuilder()
    specBuilder.builder()
    return specBuilder.build()
}
