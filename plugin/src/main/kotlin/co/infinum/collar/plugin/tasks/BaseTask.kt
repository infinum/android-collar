package co.infinum.collar.plugin.tasks

import co.infinum.genlib.GeneratorLib
import co.infinum.genlib.logging.Logger
import org.fusesource.jansi.Ansi
import org.gradle.api.DefaultTask

open class BaseTask: DefaultTask() {

    val stubGenLib: GeneratorLib
    val taskUtils: TaskUtils

    init {
        stubGenLib = GeneratorLib(logger = object : Logger {
            override fun logError(message: String) {
                logger.error(Ansi.ansi().fg(Ansi.Color.RED).a(message).reset().toString())
            }

            override fun logSuccess(message: String) {
                logger.quiet(Ansi.ansi().fg(Ansi.Color.GREEN).a(message).reset().toString())
            }

            override fun logDebug(message: String) {
                logger.debug(message)
            }

            override fun logWarning(message: String) {
                // warn will not be shown in quiet mode
                logger.error(Ansi.ansi().fg(Ansi.Color.YELLOW).a(message).reset().toString())
            }

        })
        taskUtils = TaskUtils(stubGenLib.logger)
    }
}