package co.infinum.generator

import co.infinum.generator.dependencies.MoshiModule
import co.infinum.generator.generators.EventsGenerator
import co.infinum.generator.generators.ScreensGenerator
import co.infinum.generator.generators.UserPropertiesGenerator
import co.infinum.generator.logging.Log4jLogger
import co.infinum.generator.logging.Logger
import co.infinum.generator.models.CollarModel
import co.infinum.generator.utils.FileUtils
import co.infinum.generator.utils.SynchronousExecutor
import com.squareup.moshi.JsonAdapter

class GeneratorLib(
    val logger: Logger = Log4jLogger()
) {
    fun generate(filePath: String, output: String): Boolean {
        val moshi = MoshiModule.getMoshi()
        val adapter: JsonAdapter<CollarModel> = moshi.adapter(CollarModel::class.java)

        val executor = SynchronousExecutor()
        var isSuccess = false
        executor.start(Runnable {
            adapter.fromJson(FileUtils.readFromFile(filePath))?.let {
                ScreensGenerator(it.screens, output).generate()
                UserPropertiesGenerator(it.userProperties, output).generate()
                EventsGenerator(it.events, output).generate()
            }
            isSuccess = true
        })
        executor.await()
        return isSuccess
    }
}
