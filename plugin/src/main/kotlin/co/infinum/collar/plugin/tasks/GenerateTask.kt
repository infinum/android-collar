package co.infinum.collar.plugin.tasks

import co.infinum.collar.plugin.CollarExtension
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

open class GenerateTask : BaseTask() {

    override fun getDescription(): String {
        return "Generates kotlin files for events, screen names and user properties"
    }

    @TaskAction
    fun generateStubFiles() {
        val extension: CollarExtension =
            project.extensions.findByName("collar") as CollarExtension
        if (extension.filePath.isBlank()) {
            throw  TaskExecutionException(
                this,
                Exception("Task collarGenerate failed; filePath should be specified")
            )
        }
        if (extension.outputPath.isBlank()) {
            throw  TaskExecutionException(
                this,
                Exception("Task collarGenerate failed; outputPath should be specified")
            )
        }
        taskUtils.logger.logWarning("Extension file path:")
        taskUtils.logger.logWarning(extension.filePath)
        taskUtils.logger.logWarning("Files will be generated on path:")
        taskUtils.logger.logWarning(extension.outputPath)
        taskUtils.logger.logWarning(
            stubGenLib.generate(
                extension.filePath,
                extension.outputPath
            )
        )
        taskUtils.logger.logSuccess("Done! Files are generated")
    }
}