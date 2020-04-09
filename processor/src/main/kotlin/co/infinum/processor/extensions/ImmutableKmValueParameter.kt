package co.infinum.processor.extensions

import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.hasAnnotations

@KotlinPoetMetadataPreview
fun ImmutableKmValueParameter.resolveEnabled(annotationName: String, annotationParameter: String): Boolean =
    when (this.hasAnnotations) {
        true -> {
            val annotations = this.type?.annotations.orEmpty()
            if (annotations.isEmpty()) {
                true
            } else {
                annotations.find { it.className == annotationName }
                    ?.arguments
                    ?.keys
                    ?.single { it == annotationParameter }?.toBoolean() ?: true
            }
        }
        false -> true
    }

@KotlinPoetMetadataPreview
fun ImmutableKmValueParameter.resolveName(annotationName: String, annotationParameter: String): String =
    when (this.hasAnnotations) {
        true -> {
            val annotations = this.type?.annotations.orEmpty()
            if (annotations.isEmpty()) {
                this.name.toLowerSnakeCase()
            } else {
                annotations.find { it.className == annotationName }
                    ?.arguments
                    ?.keys
                    ?.single { it == annotationParameter } ?: this.name.toLowerSnakeCase()
            }
        }
        false -> this.name.toLowerSnakeCase()
    }