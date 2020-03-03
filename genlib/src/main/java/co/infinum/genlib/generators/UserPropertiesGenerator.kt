package co.infinum.genlib.generators

import co.infinum.collar.annotations.PropertyName
import co.infinum.collar.annotations.UserProperties
import co.infinum.genlib.extensions.toCamelCase
import co.infinum.genlib.models.Property
import co.infinum.genlib.utils.PathUtils
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Paths

const val USER_PROPERTY_CLASS_NAME = "UserProperty"
const val PROPERTY_PARAMETER_NAME = "value"
const val PROPERTY_NAME_ANNOTATION_FORMAT = "value = %S"

class UserPropertiesGenerator(private val userProperties: List<Property>, private val outputPath: String) {

    fun generate() {
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
            userPropertiesClass.addType(userPropertyClass.build())
        }

        val file = FileSpec.builder(PathUtils.getPackageFromPath(outputPath), USER_PROPERTY_CLASS_NAME)
            .addType(userPropertiesClass.build())
            .build()

        file.writeTo(Paths.get(PathUtils.getOutputFromPath(outputPath)))
    }
}
