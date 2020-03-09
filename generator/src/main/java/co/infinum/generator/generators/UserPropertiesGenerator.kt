package co.infinum.generator.generators

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import co.infinum.generator.extensions.toCamelCase
import co.infinum.generator.models.Property
import co.infinum.generator.utils.PathUtils
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Paths

class UserPropertiesGenerator(private val userProperties: List<Property>, private val outputPath: String): Generator {

    companion object {
        const val USER_PROPERTY_CLASS_NAME = "UserProperty"
        const val PROPERTY_PARAMETER_NAME = "value"
        const val PROPERTY_NAME_ANNOTATION_FORMAT = "value = %S"
    }

    override fun generate() {
        val userPropertiesClass = TypeSpec.classBuilder(USER_PROPERTY_CLASS_NAME).apply {
            addAnnotation(UserProperties::class)
            addModifiers(KModifier.SEALED)
        }

        userProperties.forEach { userProperty ->
            val name = userProperty.name.toCamelCase()
            val propertyNameAnnotation = AnnotationSpec.builder(PropertyName::class)
                .addMember(PROPERTY_NAME_ANNOTATION_FORMAT, userProperty.name).build()

            val userPropertyClass = TypeSpec.classBuilder(name).apply {
                addModifiers(KModifier.DATA)
                primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter(
                            PROPERTY_PARAMETER_NAME,
                            GeneratorUtils.getClassName(userProperty)
                        )
                        .build()
                )
                addProperty(
                    PropertySpec.builder(
                        PROPERTY_PARAMETER_NAME,
                        GeneratorUtils.getClassName(userProperty)
                    )
                        .initializer(PROPERTY_PARAMETER_NAME)
                        .build()
                )

                addKdoc(userProperty.description)
                superclass(ClassName("", USER_PROPERTY_CLASS_NAME))
                addAnnotation(propertyNameAnnotation)
            }
            if (userProperty.values?.isNotEmpty() == true) {
                val enumBuilder = TypeSpec.enumBuilder(GeneratorUtils.getParameterEnumName(userProperty.name))
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

                userProperty.values.forEach { value ->
                    enumBuilder.addEnumConstant(
                        GeneratorUtils.getParameterValueEnumName(value), TypeSpec.anonymousClassBuilder()
                        .addSuperclassConstructorParameter("%S", value)
                        .build()
                    )
                }

                userPropertyClass.addType(enumBuilder.build())
            }
            userPropertiesClass.addType(userPropertyClass.build())
        }

        val file = FileSpec.builder(PathUtils.getPackageFromPath(outputPath), USER_PROPERTY_CLASS_NAME)
            .addType(userPropertiesClass.build())
            .build()

        file.writeTo(Paths.get(PathUtils.getOutputFromPath(outputPath)))
    }
}
