package co.infinum.collar.generator

import co.infinum.collar.generator.generators.EventsGenerator
import co.infinum.collar.generator.generators.Generator
import co.infinum.collar.generator.generators.ScreensGenerator
import co.infinum.collar.generator.generators.UserPropertiesGenerator
import co.infinum.collar.generator.models.AnalyticsModel
import co.infinum.collar.generator.providers.MoshiProvider
import java.io.File

internal class CollarGenerator {

    fun generate(filePath: String, output: String, packageName: String): Boolean =
        MoshiProvider.provide()
            .value
            .adapter(AnalyticsModel::class.java)
            .fromJson(File(filePath).readText(Charsets.UTF_8))
            ?.let {
                generators(it, output, packageName)
                    .fold(true, { accumulator: Boolean, generator: Generator -> accumulator && generator.generate() })
            } ?: false

    private fun generators(analyticsModel: AnalyticsModel, output: String, packageName: String) =
        with(analyticsModel) {
            listOf(
                ScreensGenerator(screens, output, packageName),
                UserPropertiesGenerator(userProperties, output, packageName),
                EventsGenerator(events, output, packageName)
            )
        }
}
