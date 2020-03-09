package co.infinum.generator.generators

import co.infinum.generator.models.Screen
import co.infinum.generator.utils.PathUtils
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Paths
import java.util.Locale

class ScreensGenerator(private val list: List<Screen>, private val outputPath: String): Generator {

    companion object {
        const val SCREENS_CLASS_NAME = "AnalyticsScreens"
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

        val file = FileSpec.builder(PathUtils.getPackageFromPath(outputPath), SCREENS_CLASS_NAME)
            .addType(screensObject.build())
            .build()

        file.writeTo(Paths.get(PathUtils.getOutputFromPath(outputPath)))
    }

    private fun prepareScreenName(name: String) = name.toUpperCase(Locale.ENGLISH).replace(" ", "_")
}