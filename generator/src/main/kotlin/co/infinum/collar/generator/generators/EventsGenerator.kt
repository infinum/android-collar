package co.infinum.collar.generator.generators

import co.infinum.collar.generator.extensions.toCamelCase
import co.infinum.collar.generator.models.Event
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class EventsGenerator(
    private val events: List<Event>,
    outputPath: String,
    packageName: String
) : CommonGenerator(outputPath, packageName, CLASS_NAME) {

    companion object {
        const val CLASS_NAME = "AnalyticsEvents"
    }

    override fun type() =
        TypeSpec.classBuilder(CLASS_NAME)
            .addAnnotation(ClassName(Generator.COLLAR_ANNOTATION_PACKAGE, Generator.COLLAR_ANNOTATION_ANALYTICS_EVENTS))
            .addModifiers(KModifier.SEALED)
            .apply {
                events.forEach { event ->
                    val name = event.name.toCamelCase()
                    val eventClass = TypeSpec.classBuilder(name)
                    val constructorBuilder = FunSpec.constructorBuilder()

                    event.parameters.forEach {
                        val eventParameterName = ClassName(Generator.COLLAR_ANNOTATION_PACKAGE, Generator.COLLAR_ANNOTATION_EVENT_PARAMETER_NAME)
                        val constructorParamAnnotation = AnnotationSpec.builder(eventParameterName)
                            .addMember(ANNOTATION_FORMAT, it.name).build()

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

                    eventClass.addModifiers(KModifier.DATA)
                    eventClass.primaryConstructor(constructorBuilder.build())
                    eventClass.addKdoc(event.description)
                    eventClass.superclass(ClassName("", CLASS_NAME))
                    eventClass.addAnnotation(
                        AnnotationSpec.builder(ClassName(Generator.COLLAR_ANNOTATION_PACKAGE, Generator.COLLAR_ANNOTATION_EVENT_NAME))
                            .addMember(ANNOTATION_FORMAT, event.name).build()
                    )

                    addType(eventClass.build())
                }
            }.build()
}