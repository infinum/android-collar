package co.infinum.processor.extensions

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isClass
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.lang.model.element.Element

@KotlinPoetMetadataPreview
fun Element.isSealedClass(): Boolean {
    val typeMetadata = this.getAnnotation(Metadata::class.java).toImmutableKmClass()
    return typeMetadata.isClass && typeMetadata.isSealed
}
