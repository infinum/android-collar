package co.infinum.collar.ui

data class Configuration(

    val showSystemNotifications: Boolean = true,

    val showInAppNotifications: Boolean = true,

    val redactedKeywords: Set<String> = setOf()
)
