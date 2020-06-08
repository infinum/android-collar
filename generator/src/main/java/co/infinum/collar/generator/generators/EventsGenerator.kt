package co.infinum.collar.generator.generators

import co.infinum.collar.generator.extensions.toCamelCase
import co.infinum.collar.generator.models.Event
import com.squareup.kotlinpoet.*
import java.nio.file.Paths

class EventsGenerator(
    private val events: List<Event>,
    private val outputPath: String,
    private val packageName: String
) : Generator {

    companion object {
        const val EVENTS_CLASS_NAME = "TrackingPlanEvents"
        const val EVENT_PARAMETER_NAME_ANNOTATION_FORMAT = "value = %S"
        const val EVENT_NAME_ANNOTATION_FORMAT = "value = %S"
    }

    override fun generate() {
        val eventsClass = TypeSpec.classBuilder(EVENTS_CLASS_NAME).apply {
            addAnnotation(ClassName(Generator.COLLAR_ANNOTATION_PACKAGE, Generator.COLLAR_ANNOTATION_ANALYTICS_EVENTS))
            addModifiers(KModifier.SEALED)
        }

        events.forEach { event ->
            val name = event.name.toCamelCase()
            val eventClass = TypeSpec.classBuilder(name)
            val constructorBuilder = FunSpec.constructorBuilder()

            event.properties?.forEach {
                val eventParameterName = ClassName(Generator.COLLAR_ANNOTATION_PACKAGE, Generator.COLLAR_ANNOTATION_EVENT_PARAMETER_NAME)
                val constructorParamAnnotation = AnnotationSpec.builder(eventParameterName)
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
                if (!event.properties.isNullOrEmpty()) {
                    addModifiers(KModifier.DATA)
                }
                primaryConstructor(constructorBuilder.build())
                addKdoc(event.description)
                superclass(ClassName("", EVENTS_CLASS_NAME))
                addAnnotation(
                    AnnotationSpec.builder(ClassName(Generator.COLLAR_ANNOTATION_PACKAGE, Generator.COLLAR_ANNOTATION_EVENT_NAME))
                        .addMember(EVENT_NAME_ANNOTATION_FORMAT, event.name).build()
                )
            }

            eventsClass.addType(eventClass.build())
        }

        val file = FileSpec.builder(packageName, EVENTS_CLASS_NAME)
            .addType(eventsClass.build())
            .build()

        file.writeTo(Paths.get(outputPath))
    }
}