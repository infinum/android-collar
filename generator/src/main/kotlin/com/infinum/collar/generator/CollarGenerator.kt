package com.infinum.collar.generator

import com.infinum.collar.generator.generators.EventsGenerator
import com.infinum.collar.generator.generators.Generator
import com.infinum.collar.generator.generators.ScreensGenerator
import com.infinum.collar.generator.generators.UserPropertiesGenerator
import com.infinum.collar.generator.models.AnalyticsModel
import java.io.File
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Generator class used to parse JSON into according Kotlin models for Collar processor consumption.
 */
public class CollarGenerator {

    /**
     * Generate models from provided JSON.
     * Parameters are passed from Collar plugin extension to a Gradle task.
     *
     * @param filePath absolute path for the provided JSON file.
     * @param output absolute path for the generated classes.
     * @param packageName package name for the generated classes.
     */
    @Suppress("PrintStackTrace")
    public fun generate(filePath: String, output: String, packageName: String): Boolean =
        try {
            Json
                .decodeFromString<AnalyticsModel>(File(filePath).readText(Charsets.UTF_8))
                .run {
                    listOf(
                        ScreensGenerator(screens, output, packageName),
                        UserPropertiesGenerator(properties, output, packageName),
                        EventsGenerator(events, output, packageName)
                    )
                        .fold(
                            initial = true,
                            operation = { accumulator: Boolean, generator: Generator ->
                                accumulator && generator.generate()
                            }
                        )
                }
        } catch (exception: SerializationException) {
            exception.printStackTrace()
            false
        }
}
