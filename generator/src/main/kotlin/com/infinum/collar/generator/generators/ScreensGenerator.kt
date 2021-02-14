package com.infinum.collar.generator.generators

import com.infinum.collar.generator.models.Screen
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.util.Locale

internal class ScreensGenerator(
    private val items: List<Screen>?,
    outputPath: String,
    packageName: String
) : CommonGenerator(outputPath, packageName, CLASS_NAME) {

    companion object {
        const val CLASS_NAME = "TrackingPlanScreens"
    }

    override fun type(): TypeSpec? =
        items?.takeIf { it.isNotEmpty() }?.let {
            TypeSpec.objectBuilder(CLASS_NAME)
                .apply {
                    it.forEach { screen ->
                        addProperty(
                            PropertySpec.builder(
                                screen.name.toUpperCase(Locale.ENGLISH).replace(" ", "_"),
                                String::class,
                                KModifier.CONST
                            )
                                .initializer("%S", screen.name)
                                .apply {
                                    screen.description
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { addKdoc(it) }
                                }
                                .build()
                        )
                    }
                }
                .build()
        }
}
