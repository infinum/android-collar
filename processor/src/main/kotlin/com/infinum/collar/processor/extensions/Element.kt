package com.infinum.collar.processor.extensions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.isClass
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toKmClass
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.NestingKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal fun Element.isSealedClass(): Boolean =
    this.getAnnotation(Metadata::class.java)
        .toKmClass()
        .run {
            this.isClass && this.flags.isSealed
        }

internal fun Element.constructorParameterNames(): List<String> =
    this.getAnnotation(Metadata::class.java)
        .toKmClass()
        .constructors
        .firstOrNull()
        ?.valueParameters
        .orEmpty()
        .map { valueParameter -> valueParameter.name }

internal fun Element.fieldElements(): List<VariableElement> =
    ElementFilter.fieldsIn(this.enclosedElements).orEmpty()

@Suppress("NestedBlockDepth", "ReturnCount")
internal fun VariableElement.isSupported(typeUtils: Types, elementUtils: Elements): Boolean {
    val type = this.asType()
    val kind = this.asType().kind

    if (kind.isPrimitive) {
        return true
    } else {
        if (kind == TypeKind.DECLARED) {
            val isString = typeUtils.isAssignable(
                type,
                elementUtils.getTypeElement(String::class.java.canonicalName).asType()
            )
            if (isString) {
                return true
            } else {
                val mapElement: TypeElement = elementUtils.getTypeElement(Map::class.java.canonicalName)
                val isMap = typeUtils.isAssignable(typeUtils.erasure(type), mapElement.asType())
                return if (isMap) {
                    val hasStringKeys = (type as? DeclaredType)?.typeArguments?.firstOrNull()?.let {
                        typeUtils.isAssignable(
                            it,
                            elementUtils.getTypeElement(String::class.java.canonicalName).asType()
                        )
                    } ?: false

                    hasStringKeys
                } else {
                    false
                }
            }
        } else {
            return false
        }
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
