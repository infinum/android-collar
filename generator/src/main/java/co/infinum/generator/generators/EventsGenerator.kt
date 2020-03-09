package co.infinum.generator.generators

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.generator.extensions.toCamelCase
import co.infinum.generator.models.Event
import co.infinum.generator.utils.PathUtils
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Paths

class EventsGenerator(private val events: List<Event>, private val outputPath: String) {

    companion object {
        const val EVENTS_CLASS_NAME = "AnalyticsEvent"
        const val EVENT_PARAMETER_NAME_ANNOTATION_FORMAT = "value = %S"
        const val EVENT_NAME_ANNOTATION_FORMAT = "value = %S"
    }

    fun generate() {
        val eventsClass = TypeSpec.classBuilder(EVENTS_CLASS_NAME).apply {
            addAnnotation(AnalyticsEvents::class)
            addModifiers(KModifier.SEALED)
        }

        events.forEach { event ->
            val name = event.name.toCamelCase()
            val eventClass = TypeSpec.classBuilder(name)
            val constructorBuilder = FunSpec.constructorBuilder()

            event.parameters.forEach {
                val constructorParamAnnotation = AnnotationSpec.builder(EventParameterName::class)
                    .addMember(EVENT_PARAMETER_NAME_ANNOTATION_FORMAT, it.name).build()

                val type = GeneratorUtils.getClassName(it)

                val constructorParamBuilder = ParameterSpec.builder(
                    GeneratorUtils.getParameterName(it.name), type
                ).apply {
                    addKdoc(it.description)
                    addAnnotation(constructorParamAnnotation)
                }
                constructorBuilder.addParameter(constructorParamBuilder.build())


                if (it.values?.isNotEmpty() == true) {
                    val enumBuilder = TypeSpec.enumBuilder(GeneratorUtils.getParameterEnumName(it.name))
                        .primaryConstructor(
                            FunSpec.constructorBuilder()
                                .addParameter("value", String::class)
                                .build()
                        )
                        .addProperty(
                            PropertySpec.builder("value", String::class)
                                .initializer("value")
                                .build()
                        )
                        .addFunction(
                            FunSpec.builder("toString")
                                .addModifiers(KModifier.OVERRIDE)
                                .addStatement("return value")
                                .build()
                        )

                    it.values.forEach { value ->
                        enumBuilder.addEnumConstant(
                            GeneratorUtils.getParameterValueEnumName(value), TypeSpec.anonymousClassBuilder()
                            .addSuperclassConstructorParameter("%S", value)
                            .build()
                        )
                    }

                    eventClass.addType(enumBuilder.build())
                }

                eventClass.addProperty(
                    PropertySpec.builder(
                        GeneratorUtils.getParameterName(it.name),
                        type
                    )
                        .initializer(GeneratorUtils.getParameterName(it.name))
                        .build()
                )
            }

            eventClass.apply {
                addModifiers(KModifier.DATA)
                primaryConstructor(constructorBuilder.build())
                addKdoc(event.description)
                superclass(ClassName("", EVENTS_CLASS_NAME))
                addAnnotation(
                    AnnotationSpec.builder(EventName::class)
                        .addMember(EVENT_NAME_ANNOTATION_FORMAT, event.name).build()
                )
            }

            eventsClass.addType(eventClass.build())
        }

        val file = FileSpec.builder(PathUtils.getPackageFromPath(outputPath), EVENTS_CLASS_NAME)
            .addType(eventsClass.build())
            .build()

        file.writeTo(Paths.get(PathUtils.getOutputFromPath(outputPath)))
    }
}