package co.infinum.collar.plugin.tasks

import co.infinum.collar.plugin.CollarExtension
import org.gradle.api.tasks.TaskAction

open class GenerateTask : BaseTask() {

    override fun getDescription(): String {
        return "Generates kotlin files for events, screen names and user properties"
    }

    @TaskAction
    fun generateStubFiles() {
        val extension: CollarExtension =
            project.extensions.findByName("collar") as CollarExtension
        taskUtils.logger.logWarning("Extension file path:")
        taskUtils.logger.logWarning(extension.filePath)
        taskUtils.logger.logWarning("Files will be generated on path:")
        taskUtils.logger.logWarning(extension.outputPath)
        taskUtils.logger.logWarning(
            stubGenLib.generateStubs(
                extension.filePath,
                extension.outputPath
            )
        )
        taskUtils.logger.logSuccess("Done! Files are generated")
    }
}