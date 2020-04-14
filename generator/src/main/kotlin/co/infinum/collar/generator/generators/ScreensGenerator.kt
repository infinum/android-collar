package co.infinum.collar.generator.generators

import co.infinum.collar.generator.models.Screen
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.util.Locale

class ScreensGenerator(
    private val items: List<Screen>,
    outputPath: String,
    packageName: String
) : CommonGenerator(outputPath, packageName, CLASS_NAME) {

    companion object {
        const val CLASS_NAME = "AnalyticsScreens"
    }

    override fun type(): TypeSpec =
        TypeSpec.objectBuilder(CLASS_NAME)
            .apply {
                items.forEach {
                    addProperty(
                        PropertySpec.builder(
                            it.name.toUpperCase(Locale.ENGLISH).replace(" ", "_"),
                            String::class,
                            KModifier.CONST
                        )
                            .initializer("%S", it.name)
                            .addKdoc(it.description)
                            .build()
                    )
                }
            }
            .build()
}