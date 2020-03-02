package co.infinum.genlib

import co.infinum.genlib.dependencies.MoshiModule
import co.infinum.genlib.logging.Log4jLogger
import co.infinum.genlib.logging.Logger
import co.infinum.genlib.models.CollarModel
import co.infinum.genlib.models.Screen
import co.infinum.genlib.utils.FileUtils
import co.infinum.genlib.utils.SynchronousExecutor
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.moshi.JsonAdapter
import java.nio.file.Paths

public class GeneratorLib(
    val logger: Logger = Log4jLogger()
) {
    fun generate(filePath: String, output: String): String {
        val moshi = MoshiModule.getMoshi()
        val adapter: JsonAdapter<CollarModel> = moshi.adapter(CollarModel::class.java)

        val executor = SynchronousExecutor()
        var string: String
        var collarModel: CollarModel? = null
        executor.start(Runnable {
            //todo make this better
            string = FileUtils.readFromFile(filePath)
            collarModel = adapter.fromJson(string)
            collarModel?.let {
                if (it.screens.isNotEmpty()) {
                    val screenObject = TypeSpec.objectBuilder("AnalyticsScreens")

                    it.screens.forEach { screen ->
                        val name = prepareScreen(screen)
                        screenObject
                            .addProperty(
                                PropertySpec.builder(name, String::class, KModifier.CONST)
                                    .initializer("%S", screen.name)
                                    .build()
                            )
                    }

                    val file = FileSpec.builder(getPackageFromPath(output), "AnalyticsScreens")
                        .addType(screenObject.build())
                        .build()

                    file.writeTo(Paths.get(getOutputFromPath(output)))
                }
            }
        })
        executor.await()
        return collarModel.toString() ?: "didn't parse"
    }

    private fun prepareScreen(screen: Screen) = screen.name.toUpperCase().replace(" ", "_")

    private fun getPackageFromPath(path: String): String {
        return when {
            path.contains("kotlin") -> {
                path.substringAfter("kotlin/").replace("/", ".")
            }
            path.contains("java") -> {
                path.substringAfter("java/").replace("/", ".")
            }
            else -> {
                logger.logError("Path is wrong")
                "aaa.asfs.co"
                //todo throw error
            }
        }
    }

    private fun getOutputFromPath(path: String): String {
        return when {
            path.contains("kotlin") -> {
                path.substringBefore("/kotlin") + "/kotlin"
            }
            path.contains("java") -> {
                path.substringBefore("/java") + "/java"
            }
            else -> {
                logger.logError("Path is wrong")
                "aaa.asfs.co"
                //todo throw error
            }
        }
    }
}
