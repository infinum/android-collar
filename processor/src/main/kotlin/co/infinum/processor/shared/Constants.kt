package co.infinum.processor.shared

import com.squareup.kotlinpoet.ClassName

internal object Constants {

    val CLASS_COMPONENT_ACTIVITY = ClassName("androidx.core.app", "ComponentActivity")
    val CLASS_ACTIVITY = ClassName("android.app", "Activity")
    val CLASS_FRAGMENT = ClassName("android.app", "Fragment")
    val CLASS_SUPPORT_FRAGMENT = ClassName("android.support.v4.app", "Fragment")
    val CLASS_ANDROIDX_FRAGMENT = ClassName("androidx.fragment.app", "Fragment")

    val SUPPORTED_SCREEN_NAME_CLASSES = listOf(
        CLASS_COMPONENT_ACTIVITY,
        CLASS_ACTIVITY,
        CLASS_FRAGMENT,
        CLASS_SUPPORT_FRAGMENT,
        CLASS_ANDROIDX_FRAGMENT
    )
}
