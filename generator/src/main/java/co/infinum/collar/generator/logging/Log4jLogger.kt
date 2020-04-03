package co.infinum.collar.generator.logging

import org.apache.logging.log4j.LogManager

class Log4jLogger : Logger {

    val logger: org.apache.logging.log4j.Logger = LogManager.getLogger("Generator")

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