package co.infinum.collar.generator.generators

import co.infinum.collar.generator.extensions.addValue
import co.infinum.collar.generator.extensions.toCamelCase
import co.infinum.collar.generator.extensions.toEnumValue
import co.infinum.collar.generator.models.DataType
import co.infinum.collar.generator.models.Event
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.util.Locale

internal class EventsGenerator(
    private val items: List<Event>?,
    outputPath: String,
    packageName: String
) : CommonGenerator(outputPath, packageName, CLASS_NAME) {

    companion object {
        const val CLASS_NAME = "TrackingPlanEvents"
    }

    @Suppress("LongMethod", "NestedBlockDepth")
    override fun type(): TypeSpec? =
        items?.takeIf { it.isNotEmpty() }?.let {
            TypeSpec.classBuilder(CLASS_NAME)
                .addAnnotation(
                    ClassName(
                        Generator.COLLAR_ANNOTATION_PACKAGE,
                        Generator.COLLAR_ANNOTATION_ANALYTICS_EVENTS
                    )
                )
                .addModifiers(KModifier.SEALED)
                .apply {
                    it.forEach { event ->
                        val name = event.name.toCamelCase()
                        val eventClass = TypeSpec.classBuilder(name)
                        val constructorBuilder = FunSpec.constructorBuilder()

                        event.parameters?.forEach { parameter ->
                            val eventParameterName = ClassName(
                                Generator.COLLAR_ANNOTATION_PACKAGE,
                                Generator.COLLAR_ANNOTATION_EVENT_PARAMETER_NAME
                            )
                            val constructorParamAnnotation = AnnotationSpec.builder(eventParameterName)
                                .addMember(ANNOTATION_FORMAT, parameter.name).build()

                            val type = when {
                                parameter.values?.isNotEmpty() == true -> DataType.TEXT.className
                                else ->
                                    DataType(parameter.type)?.className ?: error("${parameter.type} is not supported")
                            }

                            val constructorParamBuilder = ParameterSpec.builder(
                                parameter.name.toCamelCase().decapitalize(Locale.ENGLISH),
                                type
                            ).apply {
                                parameter.description
                                    ?.takeIf { it.isNotBlank() }
                                    ?.let { addKdoc(it) }
                                addAnnotation(constructorParamAnnotation)
                            }
                            constructorBuilder.addParameter(constructorParamBuilder.build())

                            if (parameter.values?.isNotEmpty() == true) {
                                val enumBuilder = TypeSpec.enumBuilder(
                                    parameter.name.toCamelCase()
                                ).addValue()

                                parameter.values.forEach { value ->
                                    enumBuilder.addEnumConstant(
                                        value.toEnumValue(),
                                        TypeSpec.anonymousClassBuilder()
                                            .addSuperclassConstructorParameter("%S", value)
                                            .build()
                                    )
                                }

                                eventClass.addType(enumBuilder.build())
                            }

                            eventClass.addProperty(
                                PropertySpec.builder(
                                    parameter.name.toCamelCase().decapitalize(Locale.ENGLISH),
                                    type
                                )
                                    .initializer(parameter.name.toCamelCase().decapitalize(Locale.ENGLISH))
                                    .build()
                            )
                        }

                        if (event.parameters.isNullOrEmpty().not()) {
                            eventClass.addModifiers(KModifier.DATA)
                        }
                        eventClass.primaryConstructor(constructorBuilder.build())
                        event.description?.takeIf { it.isNotBlank() }?.let { eventClass.addKdoc(it) }
                        eventClass.superclass(ClassName("", CLASS_NAME))
                        eventClass.addAnnotation(
                            AnnotationSpec.builder(
                                ClassName(
                                    Generator.COLLAR_ANNOTATION_PACKAGE,
                                    Generator.COLLAR_ANNOTATION_EVENT_NAME
                                )
                            )
                                .addMember(ANNOTATION_FORMAT, event.name).build()
                        )

                        addType(eventClass.build())
                    }
                }.build()
        }
}
