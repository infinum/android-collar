package co.infinum.collar.processor.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isClass
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.NestingKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter

@KotlinPoetMetadataPreview
internal fun Element.isSealedClass(): Boolean =
    this.getAnnotation(Metadata::class.java)
        .toImmutableKmClass()
        .run {
            this.isClass && this.isSealed
        }

@KotlinPoetMetadataPreview
internal fun Element.constructorParameterNames(): List<String> =
    this.getAnnotation(Metadata::class.java)
        .toImmutableKmClass()
        .constructors
        .firstOrNull()
        ?.valueParameters
        .orEmpty()
        .map { valueParameter -> valueParameter.name }

internal fun Element.fieldElements(): List<VariableElement> =
    ElementFilter.fieldsIn(this.enclosedElements).orEmpty()

internal fun VariableElement.resolveMethod(): String =
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

internal fun TypeElement.asClassName(): ClassName {
    fun isClassOrInterface(e: Element) = e.kind.isClass || e.kind.isInterface

    fun getPackage(type: Element): PackageElement {
        var t = type
        while (t.kind != ElementKind.PACKAGE) {
            t = t.enclosingElement
        }
        return t as PackageElement
    }

    val names = mutableListOf<String>()
    var e: Element = this
    while (isClassOrInterface(e)) {
        val eType = e as TypeElement
        require(
            eType.nestingKind == NestingKind.TOP_LEVEL || eType.nestingKind == NestingKind.MEMBER
        ) {
            "unexpected type testing"
        }
        names += eType.simpleName.toString()
        e = eType.enclosingElement
    }

    val packageName = getPackage(this).qualifiedName.toString()
    names.reverse()

    return ClassName(packageName, names)
}
