package co.infinum.collar.ui

public data class Configuration(

    val analyticsCollectionEnabled: Boolean = true,

    val showSystemNotifications: Boolean = true,

    val showInAppNotifications: Boolean = true,

    val redactedKeywords: Set<String> = setOf()
)
