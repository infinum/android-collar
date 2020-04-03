package co.infinum.collar.generator

import co.infinum.collar.generator.dependencies.MoshiModule
import co.infinum.collar.generator.generators.EventsGenerator
import co.infinum.collar.generator.generators.ScreensGenerator
import co.infinum.collar.generator.generators.UserPropertiesGenerator
import co.infinum.collar.generator.models.AnalyticsModel
import co.infinum.collar.generator.utils.FileUtils
import com.squareup.moshi.JsonAdapter

class GeneratorLib {
    fun generate(filePath: String, output: String, packageName: String): Boolean {
        val moshi = MoshiModule.getMoshi()
        val adapter: JsonAdapter<AnalyticsModel> = moshi.adapter(AnalyticsModel::class.java)

        var isSuccess = false
        adapter.fromJson(FileUtils.readFromFile(filePath))?.let { analyticsModel ->
            listOf(
                ScreensGenerator(analyticsModel.screens, output, packageName),
                UserPropertiesGenerator(analyticsModel.userProperties, output, packageName),
                EventsGenerator(analyticsModel.events, output, packageName)
            ).forEach { it.generate() }
        }
        isSuccess = true
        return isSuccess
    }
}
