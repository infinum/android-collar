package com.infinum.collar.generator.extensions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal fun TypeSpec.Builder.addValue(parameterName: String = "parameter"): TypeSpec.Builder {
    return this.primaryConstructor(
        FunSpec.constructorBuilder()
            .addParameter(parameterName, String::class)
            .build()
    )
        .addProperty(
            PropertySpec.builder(parameterName, String::class)
                .initializer(parameterName)
                .build()
        )
        .addFunction(
            FunSpec.builder("toString")
                .addModifiers(KModifier.OVERRIDE)
                .returns(String::class)
                .addStatement("return $parameterName")
                .build(),
        )
}
