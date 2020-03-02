package co.infinum.genlib

import co.infinum.genlib.dependencies.MoshiModule
import co.infinum.genlib.logging.Log4jLogger
import co.infinum.genlib.logging.Logger
import co.infinum.genlib.models.CollarModel
import co.infinum.genlib.utils.FileUtils
import co.infinum.genlib.utils.SynchronousExecutor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.moshi.JsonAdapter
import java.nio.file.Paths

public class GeneratorLib(
    val logger: Logger = Log4jLogger()
) {
    fun generateStubs(filePath: String, output: String): String {
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
                    val greeterClass = ClassName("", "Screens")
                    val file = FileSpec.builder("", "HelloWorld")
                        .addType(
                            TypeSpec.classBuilder("Screen")
                                .primaryConstructor(
                                    FunSpec.constructorBuilder()
                                        .addParameter("name", String::class)
                                        .build()
                                )
                                .addProperty(
                                    PropertySpec.builder("name", String::class)
                                        .initializer("name")
                                        .build()
                                )
                                .build()
                        )
                        .build()

                    file.writeTo(Paths.get(output))
                }
            }
        })
        executor.await()
        return collarModel.toString() ?: "didn't parse"
    }
}
