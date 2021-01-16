package co.infinum.collar.ui

public data class Configuration(

    val analyticsCollectionEnabled: Boolean = false,

    val showSystemNotifications: Boolean = false,

    val showInAppNotifications: Boolean = false,

    val redactedKeywords: Set<String> = setOf()
)
