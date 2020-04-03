package co.infinum.collar.plugin.tasks

import co.infinum.collar.generator.logging.Logger
import java.util.*

class TaskUtils(val logger: Logger) {

    private val scanner: Scanner = Scanner(System.`in`)

    fun parameterOrDefault(parameter: String?, defaultValue: String): String {
        return if (parameter.isNullOrEmpty()) defaultValue else parameter
    }

    fun logWarning(message: String, prefix: String = "", suffix: String = "") {
        logger.logWarning(getFormattedString(message, prefix, suffix))
    }

    fun logError(message: String, prefix: String = "", suffix: String = "") {
        logger.logError(getFormattedString(message, prefix, suffix))
    }

    fun logQuiet(message: String, prefix: String = "", suffix: String = "") {
        logger.logSuccess(getFormattedString(message, prefix, suffix))
    }

    fun printToConsole(message: String, prefix: String = "", suffix: String = "\n") {
        print(getFormattedString(message, prefix, suffix))
    }

    private fun getFormattedString(message: String, prefix: String, suffix: String) = "$prefix$message$suffix"

    fun printNewLine() {
        printToConsole("")
    }

    fun readLine(message: String): String {
        printToConsole(message)
        System.out.flush()
        try {
            return scanner.nextLine()
        } catch (e: NoSuchElementException) {
            return ""
        }
    }

    fun closeScanner() {
        scanner.close()
    }

}