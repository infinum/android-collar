package com.infinum.collar.generator.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

internal interface Generator {

    companion object {
        const val COLLAR_ANNOTATION_PACKAGE = "com.infinum.collar.annotations"
        const val COLLAR_ANNOTATION_USER_PROPERTIES = "UserProperties"
        const val COLLAR_ANNOTATION_PROPERTY_NAME = "PropertyName"
        const val COLLAR_ANNOTATION_ANALYTICS_EVENTS = "AnalyticsEvents"
        const val COLLAR_ANNOTATION_EVENT_PARAMETER_NAME = "EventParameterName"
        const val COLLAR_ANNOTATION_EVENT_NAME = "EventName"
    }

    fun generate(): Boolean

    fun file(): FileSpec.Builder

    fun type(): TypeSpec?

    fun write(fileSpec: FileSpec)
}
