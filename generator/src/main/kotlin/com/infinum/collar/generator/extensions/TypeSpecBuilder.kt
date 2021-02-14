package com.infinum.collar.generator.extensions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal fun TypeSpec.Builder.addValue() =
    this.primaryConstructor(
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
