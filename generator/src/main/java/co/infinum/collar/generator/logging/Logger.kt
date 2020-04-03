package co.infinum.collar.generator.logging

interface Logger {

    fun logError(message: String)

    fun logSuccess(message: String)

    fun logDebug(message: String)

    fun logWarning(message: String)

}