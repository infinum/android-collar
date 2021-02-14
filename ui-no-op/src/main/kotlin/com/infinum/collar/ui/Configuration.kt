package com.infinum.collar.ui

/**
 * Implementation of Collector configuration.
 *
 * @param analyticsCollectionEnabled is false by default.
 * @param showSystemNotifications is false by default.
 * @param showInAppNotifications is false by default.
 * @param redactedKeywords is an empty set by default.
 */
public data class Configuration(

    val analyticsCollectionEnabled: Boolean = false,

    val showSystemNotifications: Boolean = false,

    val showInAppNotifications: Boolean = false,

    val redactedKeywords: Set<String> = setOf()
)
