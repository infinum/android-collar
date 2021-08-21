package com.infinum.collar.ui

/**
 * Implementation of Collector configuration.
 *
 * @param analyticsCollectionEnabled is true by default.
 * @param showSystemNotifications is true by default.
 * @param showInAppNotifications is true by default.
 * @param redactedKeywords is an empty set by default.
 */
public data class Configuration(

    val analyticsCollectionEnabled: Boolean = true,

    val showSystemNotifications: Boolean = true,

    val showInAppNotifications: Boolean = true,

    val redactedKeywords: Set<String> = setOf()
)
