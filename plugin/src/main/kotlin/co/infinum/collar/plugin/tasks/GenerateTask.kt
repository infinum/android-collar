package co.infinum.collar.plugin.tasks

import co.infinum.collar.plugin.CollarConstants.COLLAR_EXTENSION
import co.infinum.collar.plugin.CollarExtension
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

open class GenerateTask : BaseTask() {

    @TaskAction
    fun generateFiles() {
        val extension: CollarExtension =
            project.extensions.findByName(COLLAR_EXTENSION) as CollarExtension
        if (extension.filePath.isBlank()) {
            throw TaskExecutionException(
                this,
                Exception("Task generate failed; filePath should be specified")
            )
        }
        if (extension.outputPath.isBlank()) {
            throw TaskExecutionException(
                this,
                Exception("Task generate failed; outputPath should be specified")
            )
        }
        println("Extension file path:")
        println(extension.filePath)
        println("Files will be generated on path:")
        println(extension.outputPath)
        try {
            if (generatorLib.generate(extension.filePath, extension.outputPath)) {
                println("Done! Files are generated")
            } else {
                println("Task generate failed;")
                throw  TaskExecutionException(
                    this,
                    Exception("Task generate failed;")
                )
            }
        } catch (e: Exception) {
            println(e.stackTrace.toString())
            throw  TaskExecutionException(
                this,
                Exception("Task generate failed;")
            )
        }
    }
}