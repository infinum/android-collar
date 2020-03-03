package co.infinum.genlib.generators

import co.infinum.collar.annotations.AnalyticsEvents
import co.infinum.collar.annotations.EventName
import co.infinum.collar.annotations.EventParameterName
import co.infinum.genlib.extensions.toCamelCase
import co.infinum.genlib.models.Event
import co.infinum.genlib.utils.PathUtils
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Paths

const val EVENTS_CLASS_NAME = "AnalyticsEvent"
const val EVENT_PARAMETER_NAME_ANNOTATION_FORMAT = "value = %S"
const val EVENT_NAME_ANNOTATION_FORMAT = "value = %S"

class EventsGenerator(private val events: List<Event>, private val outputPath: String) {

    fun generate() {
        val eventsClass = TypeSpec.classBuilder(EVENTS_CLASS_NAME).apply {
            addAnnotation(AnalyticsEvents::class)
            addModifiers(KModifier.SEALED)
        }

        events.forEach { event ->
            val name = event.name.toCamelCase()
            val eventClass = TypeSpec.classBuilder(name)
            val constructorBuilder = FunSpec.constructorBuilder()

            event.properties.forEach {
                val constructorParamAnnotation = AnnotationSpec.builder(EventParameterName::class)
                    .addMember(EVENT_PARAMETER_NAME_ANNOTATION_FORMAT, it.name).build()
                val constructorParamBuilder = ParameterSpec.builder(
                    it.name.toCamelCase().decapitalize(),
                    GeneratorUtils.getClassNameFromTypeAndListType(it.type, it.listType)
                ).apply {
                    addKdoc(it.description)
                    addAnnotation(constructorParamAnnotation)
                }
                constructorBuilder.addParameter(constructorParamBuilder.build())

                eventClass.addProperty(
                    PropertySpec.builder(
                        it.name.toCamelCase().decapitalize(),
                        GeneratorUtils.getClassNameFromTypeAndListType(it.type, it.listType)
                    )
                        .initializer(it.name.toCamelCase().decapitalize())
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