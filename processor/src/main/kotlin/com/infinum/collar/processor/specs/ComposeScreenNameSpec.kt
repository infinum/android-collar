package com.infinum.collar.processor.specs

import com.infinum.collar.processor.models.ComposeScreenHolder
import com.squareup.kotlinpoet.FunSpec
import java.io.File

internal class ComposeScreenNameSpec(
    outputDir: File,
    packageName: String,
    private val simpleName: String,
    private val holder: ComposeScreenHolder
) : CommonSpec(outputDir, packageName, simpleName) {

    companion object {
        private const val FUNCTION_TRACK_PREFIX = "track"
        private const val FUNCTION_TRACK_SCREEN = "trackScreen"
        private const val PARAMETER_NAME = "this"
        private const val STATEMENT = "%T.%L(%S)"
    }


    override fun parameterName(): String = PARAMETER_NAME

    override fun extensions(): List<FunSpec> {
        return listOf(
            FunSpec.builder("$FUNCTION_TRACK_PREFIX$simpleName")
                .apply {
                    addStatement(this, holder)
                }
                .build()
        )
    }


    private fun addStatement(builder: FunSpec.Builder, holder: ComposeScreenHolder) =
        builder.addStatement(
            STATEMENT,
            CLASS_COLLAR,
            FUNCTION_TRACK_SCREEN,
            holder.screenName
        )
}
