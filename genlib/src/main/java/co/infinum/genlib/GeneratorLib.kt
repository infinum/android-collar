package co.infinum.genlib

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import co.infinum.genlib.dependencies.MoshiModule
import co.infinum.genlib.logging.Log4jLogger
import co.infinum.genlib.logging.Logger
import co.infinum.genlib.models.CollarModel
import co.infinum.genlib.utils.FileUtils
import co.infinum.genlib.utils.SynchronousExecutor
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
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
            string = FileUtils.readFromFile(filePath)
            collarModel = adapter.fromJson(string)
            collarModel?.let {
                if (it.screens.isNotEmpty()) {
                    generateScreens(it, output)
                }

                if (it.userProperties.isNotEmpty()) {
                    generateUserProperties(it, output)
                }

                if (it.events.isNotEmpty()) {
                    //todo
                }
            }
        })
        executor.await()
        return collarModel.toString() ?: "didn't parse"
    }

    private fun generateUserProperties(it: CollarModel, output: String) {
        val userPropertyClass = TypeSpec.classBuilder("UserProperty")
        userPropertyClass.addAnnotation(UserProperties::class)
        userPropertyClass.addModifiers(KModifier.SEALED)

        it.userProperties.forEach { userProperty ->
            val name = userProperty.name.toCamelCase()
            val propertyClass = TypeSpec.classBuilder(name)
            propertyClass.addModifiers(KModifier.DATA)
            propertyClass.primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("value", String::class)
                    .build()
            )
            propertyClass.addProperty(PropertySpec.builder("value", String::class)
                            .initializer("value")
                            .build())

            propertyClass.addKdoc(userProperty.description)
            propertyClass.superclass(ClassName("", "UserProperty"))
            val propertyNameAnnotation = AnnotationSpec.builder(PropertyName::class)
                .addMember("value = %S", userProperty.name).build()

            propertyClass.addAnnotation(propertyNameAnnotation)
            userPropertyClass
                .addType(
                    propertyClass
                        .build()
                )
        }

        val file = FileSpec.builder(getPackageFromPath(output), "UserProperty")
            .addType(userPropertyClass.build())
            .build()

        file.writeTo(Paths.get(getOutputFromPath(output)))
    }

    private fun generateScreens(it: CollarModel, output: String) {
        val screenObject = TypeSpec.objectBuilder("AnalyticsScreens")

        it.screens.forEach { screen ->
            val name = prepareScreen(screen.name)
            screenObject
                .addProperty(
                    PropertySpec.builder(name, String::class, KModifier.CONST)
                        .initializer("%S", screen.name)
                        .addKdoc(screen.description)
                        .build()
                )
        }

        val file = FileSpec.builder(getPackageFromPath(output), "AnalyticsScreens")
            .addType(screenObject.build())
            .build()

        file.writeTo(Paths.get(getOutputFromPath(output)))
    }

    private fun prepareScreen(property: String) = property.toUpperCase().replace(" ", "_")

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

    private fun String.toCamelCase(): String = split(" ").map { it.capitalize() }.joinToString("")

}
