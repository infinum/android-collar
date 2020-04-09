package co.infinum.processor.extensions

import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isClass
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.lang.model.element.Element

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

@KotlinPoetMetadataPreview
fun Element.constructorParameters(): List<ImmutableKmValueParameter> =
    this.getAnnotation(Metadata::class.java)
        .toImmutableKmClass()
        .constructors
        .firstOrNull()
        ?.valueParameters
        .orEmpty()