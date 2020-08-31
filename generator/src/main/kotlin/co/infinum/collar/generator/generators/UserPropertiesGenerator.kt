package co.infinum.collar.generator.generators

import co.infinum.collar.generator.extensions.toCamelCase
import co.infinum.collar.generator.extensions.toEnumValue
import co.infinum.collar.generator.generators.Generator.Companion.COLLAR_ANNOTATION_PACKAGE
import co.infinum.collar.generator.generators.Generator.Companion.COLLAR_ANNOTATION_PROPERTY_NAME
import co.infinum.collar.generator.generators.Generator.Companion.COLLAR_ANNOTATION_USER_PROPERTIES
import co.infinum.collar.generator.models.Property
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal class UserPropertiesGenerator(
    private val items: List<Property>?,
    outputPath: String,
    packageName: String
) : CommonGenerator(outputPath, packageName, CLASS_NAME) {

    companion object {
        const val CLASS_NAME = "TrackingPlanUserProperties"
        const val PROPERTY_PARAMETER_NAME = "value"
    }

    @Suppress("LongMethod", "NestedBlockDepth")
    override fun type(): TypeSpec? =
        items?.takeIf { it.isNotEmpty() }?.let {
            TypeSpec.classBuilder(CLASS_NAME)
                .addAnnotation(ClassName(COLLAR_ANNOTATION_PACKAGE, COLLAR_ANNOTATION_USER_PROPERTIES))
                .addModifiers(KModifier.SEALED)
                .apply {
                    it.forEach { userProperty ->
                        val name = userProperty.name.toCamelCase()
                        val propertyNameClassName = ClassName(
                            COLLAR_ANNOTATION_PACKAGE,
                            COLLAR_ANNOTATION_PROPERTY_NAME
                        )
                        val propertyNameAnnotation = AnnotationSpec.builder(propertyNameClassName)
                            .addMember(ANNOTATION_FORMAT, userProperty.name).build()
                        val userPropertyClass = TypeSpec.classBuilder(name).apply {
                            addModifiers(KModifier.DATA)
                            primaryConstructor(
                                FunSpec.constructorBuilder()
                                    .addParameter(
                                        PROPERTY_PARAMETER_NAME,
                                        ClassName("kotlin", "String")
                                    )
                                    .build()
                            )
                            addProperty(
                                PropertySpec.builder(
                                    PROPERTY_PARAMETER_NAME,
                                    ClassName("kotlin", "String")
                                )
                                    .initializer(PROPERTY_PARAMETER_NAME)
                                    .build()
                            )

                            userProperty.description?.takeIf { it.isNotBlank() }?.let { addKdoc(it) }
                            superclass(ClassName("", CLASS_NAME))
                            addAnnotation(propertyNameAnnotation)
                        }
                        if (userProperty.values?.isNotEmpty() == true) {
                            val enumBuilder = TypeSpec.enumBuilder(
                                userProperty.name.toCamelCase()
                            )
                                .primaryConstructor(
                                    FunSpec.constructorBuilder()
                                        .addParameter("value", String::class, KModifier.PRIVATE)
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

                            userProperty.values.forEach { value ->
                                enumBuilder.addEnumConstant(
                                    value.toEnumValue(),
                                    TypeSpec.anonymousClassBuilder()
                                        .addSuperclassConstructorParameter("%S", value)
                                        .build()
                                )
                            }

                            userPropertyClass.addType(enumBuilder.build())
                        }

                        addType(userPropertyClass.build())
                    }
                }
                .build()
        }
}
