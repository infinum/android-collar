package co.infinum.processor.extensions

import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.hasAnnotations
import kotlinx.metadata.KmClassifier

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
fun ImmutableKmValueParameter.resolveMethod(): String {
    return (type?.classifier as? KmClassifier.Class)?.let {
        when (it.name) {
            "kotlin/String" -> "putString"
            "kotlin/Boolean" -> "putBoolean"
            "kotlin/Byte" -> "putByte"
            "kotlin/Char" -> "putChar"
            "kotlin/Double" -> "putDouble"
            "kotlin/Float" -> "putFloat"
            "kotlin/Int" -> "putInt"
            "kotlin/Long" -> "putLong"
            "kotlin/Short" -> "putShort"
            "android/os/Bundle" -> "putBundle"
            else -> ""
        }
    }.orEmpty()
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