package co.infinum.collar.generator.generators

import co.infinum.collar.generator.models.Screen
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Paths
import java.util.*

class ScreensGenerator(
    private val list: List<Screen>,
    private val outputPath: String,
    private val packageName: String
): Generator {

    companion object {
        const val SCREENS_CLASS_NAME = "TrackingPlanScreens"
    }

    override fun generate() {
        val screensObject = TypeSpec.objectBuilder(SCREENS_CLASS_NAME)

        list.forEach { screen ->
            val name = prepareScreenName(screen.name)
            screensObject
                .addProperty(
                    PropertySpec.builder(name, String::class, KModifier.CONST)
                        .initializer("%S", screen.name)
                        .addKdoc(screen.description)
                        .build()
                )
        }

        val file = FileSpec.builder(packageName, SCREENS_CLASS_NAME)
            .addType(screensObject.build())
            .build()

        file.writeTo(Paths.get(outputPath))
    }

    private fun prepareScreenName(name: String) = name.toUpperCase(Locale.ENGLISH).replace(" ", "_")
}