package co.infinum.genlib

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
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
import com.squareup.kotlinpoet.ParameterSpec
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
                    generateEvents(it, output)
                }
            }
        })
        executor.await()
        return collarModel.toString() ?: "didn't parse"
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

    private fun generateUserProperties(it: CollarModel, output: String) {
        val userPropertiesClass = TypeSpec.classBuilder("UserProperty")
        userPropertiesClass.addAnnotation(UserProperties::class)
        userPropertiesClass.addModifiers(KModifier.SEALED)

        it.userProperties.forEach { userProperty ->
            val name = userProperty.name.toCamelCase()
            val userPropertyClass = TypeSpec.classBuilder(name)
            userPropertyClass.addModifiers(KModifier.DATA)
            userPropertyClass.primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("value", String::class)
                    .build()
            )
            userPropertyClass.addProperty(
                PropertySpec.builder("value", String::class)
                    .initializer("value")
                    .build()
            )

            userPropertyClass.addKdoc(userProperty.description)
            userPropertyClass.superclass(ClassName("", "UserProperty"))
            val propertyNameAnnotation = AnnotationSpec.builder(PropertyName::class)
                .addMember("value = %S", userProperty.name).build()

            userPropertyClass.addAnnotation(propertyNameAnnotation)
            userPropertiesClass
                .addType(
                    userPropertyClass
                        .build()
                )
        }

        val file = FileSpec.builder(getPackageFromPath(output), "UserProperty")
            .addType(userPropertiesClass.build())
            .build()

        file.writeTo(Paths.get(getOutputFromPath(output)))
    }

    private fun generateEvents(it: CollarModel, output: String) {
        val eventsClass = TypeSpec.classBuilder("AnalyticsEvent")
        eventsClass.addAnnotation(AnalyticsEvents::class)
        eventsClass.addModifiers(KModifier.SEALED)

        it.events.forEach { event ->
            val name = event.name.toCamelCase()
            val eventClass = TypeSpec.classBuilder(name)
            eventClass.addModifiers(KModifier.DATA)
            val constructorBuilder = FunSpec.constructorBuilder()
            event.properties.forEach {
                val constructorParamBuild = ParameterSpec.builder(it.name.toCamelCase().decapitalize(), String::class)
                constructorParamBuild.addKdoc(it.description)
                val constructorParamAnnot = AnnotationSpec.builder(EventParameterName::class)
                    .addMember("value = %S", it.name).build()
                constructorParamBuild.addAnnotation(constructorParamAnnot)
                val constParam = constructorParamBuild.build()
                constructorBuilder.addParameter(constParam)

                eventClass.addProperty(
                    PropertySpec.builder(it.name.toCamelCase().decapitalize(), String::class)
                        .initializer(it.name.toCamelCase().decapitalize())
                        .build()
                )
            }
            eventClass.primaryConstructor(constructorBuilder.build())

            eventClass.addKdoc(event.description)
            eventClass.superclass(ClassName("", "AnalyticsEvent"))
            val eventNameAnnotation = AnnotationSpec.builder(EventName::class)
                .addMember("value = %S", event.name).build()

            eventClass.addAnnotation(eventNameAnnotation)
            eventsClass
                .addType(
                    eventClass
                        .build()
                )
        }

        val file = FileSpec.builder(getPackageFromPath(output), "AnalyticsEvent")
            .addType(eventsClass.build())
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
