package co.infinum.collar.generator

import co.infinum.collar.generator.generators.EventsGenerator
import co.infinum.collar.generator.generators.Generator
import co.infinum.collar.generator.generators.ScreensGenerator
import co.infinum.collar.generator.generators.UserPropertiesGenerator
import co.infinum.collar.generator.models.AnalyticsModel
import co.infinum.collar.generator.providers.MoshiProvider
import java.io.File

class CollarGenerator {

    fun generate(fileName: String, output: String, packageName: String): Boolean =
        MoshiProvider.get()
            .value
            .adapter(AnalyticsModel::class.java)
            .fromJson(File(fileName).readText(Charsets.UTF_8))
            ?.let { analyticsModel ->
                listOf(
                    ScreensGenerator(analyticsModel.screens, output, packageName),
                    UserPropertiesGenerator(analyticsModel.userProperties, output, packageName),
                    EventsGenerator(analyticsModel.events, output, packageName)
                ).fold(true, { accumulator: Boolean, generator: Generator -> accumulator && generator.generate() })
            } ?: false
}
