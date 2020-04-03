package co.infinum.collar.plugin.tasks

import co.infinum.collar.generator.GeneratorLib
import co.infinum.collar.generator.logging.Logger
import org.gradle.api.DefaultTask

open class BaseTask: DefaultTask() {

    val generatorLib: GeneratorLib
    val taskUtils: TaskUtils

    init {
        generatorLib = GeneratorLib(logger = object : Logger {
            override fun logError(message: String) {
                logger.error(message)
            }

            override fun logSuccess(message: String) {
                logger.quiet(message)
            }

            override fun logDebug(message: String) {
                logger.debug(message)
            }

            override fun logWarning(message: String) {
                // warn will not be shown in quiet mode
                logger.error(message)
            }

        })
        taskUtils = TaskUtils(generatorLib.logger)
    }
}