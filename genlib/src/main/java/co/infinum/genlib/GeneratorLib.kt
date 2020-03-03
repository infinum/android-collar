package co.infinum.genlib

import co.infinum.genlib.dependencies.MoshiModule
import co.infinum.genlib.generators.EventsGenerator
import co.infinum.genlib.generators.ScreensGenerator
import co.infinum.genlib.generators.UserPropertiesGenerator
import co.infinum.genlib.logging.Log4jLogger
import co.infinum.genlib.logging.Logger
import co.infinum.genlib.models.CollarModel
import co.infinum.genlib.utils.FileUtils
import co.infinum.genlib.utils.SynchronousExecutor
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
