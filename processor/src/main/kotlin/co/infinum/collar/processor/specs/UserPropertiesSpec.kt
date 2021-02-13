package co.infinum.collar.processor.specs

import co.infinum.collar.processor.extensions.applyIf
import co.infinum.collar.processor.models.PropertyHolder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import java.io.File

internal class UserPropertiesSpec(
    outputDir: File,
    private val className: ClassName,
    private val holders: Set<PropertyHolder>
) : CommonSpec(outputDir, className.packageName, className.simpleName) {

    companion object {
        private const val SIMPLE_NAME = "UserProperties"
        private const val FUNCTION_TRACK_PROPERTY = "trackProperty"
        private const val STATEMENT = "is %T -> %T.%L(%S, %L.%L)"
    }

    override fun extensions(): List<FunSpec> =
        listOf(
            FunSpec.builder(FUNCTION_TRACK_PROPERTY)
                .addParameter(parameterName(), className)
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
