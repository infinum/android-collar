package co.infinum.genlib.logging

import org.apache.logging.log4j.LogManager

class Log4jLogger : Logger {

    val logger: org.apache.logging.log4j.Logger = LogManager.getLogger("Polyglot")

    override fun logError(message: String) {
        logger.error(message)
    }

    override fun logSuccess(message: String) {
        logger.info(message)
    }

    override fun logDebug(message: String) {
        logger.debug(message)
    }

    override fun logWarning(message: String) {
        logger.warn(message)
    }
}