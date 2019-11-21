package co.infinum.processor.models

import com.squareup.kotlinpoet.ClassName

data class DeclaredAnalyticsEvent(
    val className: ClassName,
    val resolvedName: String
)