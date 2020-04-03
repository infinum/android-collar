package co.infinum.collar.generator

import co.infinum.collar.generator.dependencies.MoshiModule
import co.infinum.collar.generator.generators.EventsGenerator
import co.infinum.collar.generator.generators.ScreensGenerator
import co.infinum.collar.generator.generators.UserPropertiesGenerator
import co.infinum.collar.generator.logging.Log4jLogger
import co.infinum.collar.generator.logging.Logger
import co.infinum.collar.generator.models.AnalyticsModel
import co.infinum.collar.generator.utils.FileUtils
import co.infinum.collar.generator.utils.SynchronousExecutor
import com.squareup.moshi.JsonAdapter

class GeneratorLib(
    val logger: Logger = Log4jLogger()
) {
    fun generate(filePath: String, output: String): Boolean {
        val moshi = MoshiModule.getMoshi()
        val adapter: JsonAdapter<AnalyticsModel> = moshi.adapter(AnalyticsModel::class.java)

        val executor = SynchronousExecutor()
        var isSuccess = false
        executor.start(Runnable {
            adapter.fromJson(FileUtils.readFromFile(filePath))?.let { analyticsModel ->
                listOf(
                    ScreensGenerator(analyticsModel.screens, output),
                    UserPropertiesGenerator(analyticsModel.userProperties, output),
                    EventsGenerator(analyticsModel.events, output)
                ).forEach { it.generate() }
            }
            isSuccess = true
        })
        executor.await()
        return isSuccess
    }
}
