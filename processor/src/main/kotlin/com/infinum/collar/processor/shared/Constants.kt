package com.infinum.collar.processor.shared

import com.squareup.kotlinpoet.ClassName

internal object Constants {

    val CLASS_FRAGMENT = ClassName("android.app", "Fragment")
    val CLASS_SUPPORT_FRAGMENT = ClassName("android.support.v4.app", "Fragment")
    private val CLASS_COMPONENT_ACTIVITY = ClassName("androidx.core.app", "ComponentActivity")
    private val CLASS_ACTIVITY = ClassName("android.app", "Activity")
    private val CLASS_ANDROIDX_FRAGMENT = ClassName("androidx.fragment.app", "Fragment")

    val SUPPORTED_SCREEN_NAME_CLASSES = listOf(
        CLASS_COMPONENT_ACTIVITY,
        CLASS_ACTIVITY,
        CLASS_FRAGMENT,
        CLASS_SUPPORT_FRAGMENT,
        CLASS_ANDROIDX_FRAGMENT
    )
}
