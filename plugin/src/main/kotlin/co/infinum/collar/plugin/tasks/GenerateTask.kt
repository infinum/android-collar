package co.infinum.collar.plugin.tasks

import co.infinum.collar.plugin.CollarConstants.COLLAR_EXTENSION
import co.infinum.collar.plugin.CollarExtension
import co.infinum.collar.plugin.checkIfValid
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

open class GenerateTask : BaseTask() {

    @TaskAction
    fun generateFiles() {
        val extension: CollarExtension =
            project.extensions.findByName(COLLAR_EXTENSION) as CollarExtension

        val errors = extension.checkIfValid()

        if (errors.isNotEmpty()) {
            throw TaskExecutionException(
                this,
                Exception(errors.joinToString("\n"))
            )
        }

        println("Files will be generated on path:")
        val outputPath = "${project.projectDir}/src/${extension.module}/kotlin/"
        println(outputPath)

        println("Extension file path:")
        val filePath = "${project.projectDir}/${extension.fileName}"
        println(filePath)

        try {
            if (generatorLib.generate(filePath, outputPath, extension.packageName)) {
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