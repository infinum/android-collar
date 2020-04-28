package co.infinum.processor.extensions

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isClass
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter

@KotlinPoetMetadataPreview
fun Element.isSealedClass(): Boolean =
    this.getAnnotation(Metadata::class.java)
        .toImmutableKmClass()
        .run {
            this.isClass && this.isSealed
        }

@KotlinPoetMetadataPreview
fun Element.constructorParameterNames(): List<String> =
    this.getAnnotation(Metadata::class.java)
        .toImmutableKmClass()
        .constructors
        .firstOrNull()
        ?.valueParameters
        .orEmpty()
        .map { valueParameter -> valueParameter.name }

fun Element.fieldElements(): List<VariableElement> =
    ElementFilter.fieldsIn(this.enclosedElements).orEmpty()

fun VariableElement.resolveMethod(): String =
    this.asType().toString().let {
        when (it) {
            "java.lang.String" -> "putString"
            "boolean" -> "putBoolean"
            "byte" -> "putByte"
            "char" -> "putChar"
            "double" -> "putDouble"
            "float" -> "putFloat"
            "int" -> "putInt"
            "long" -> "putLong"
            "short" -> "putShort"
            "android.os.Bundle" -> "putBundle"
            else -> ""
        }
    }
