package co.infinum.collar.ui

data class Configuration(

    val showSystemNotifications: Boolean = false,

    val showInAppNotifications: Boolean = false,

    val redactedKeywords: Set<String> = setOf()
)
