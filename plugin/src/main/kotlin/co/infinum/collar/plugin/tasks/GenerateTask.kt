package co.infinum.collar.plugin.tasks

import co.infinum.collar.generator.CollarGenerator
import co.infinum.collar.plugin.CollarExtension
import co.infinum.collar.plugin.tasks.shared.BaseTask
import co.infinum.collar.plugin.validate
import org.gradle.api.tasks.TaskAction

open class GenerateTask : BaseTask() {

    companion object {
        const val GROUP = "collar"
        const val NAME = "generate"
        const val DESCRIPTION = "Generates Kotlin files for screen names, events and user properties."
    }

    private val collarGenerator = CollarGenerator()

    @TaskAction
    fun generateTrackingPlanClasses() {
        (project.extensions.findByName(CollarExtension.NAME) as CollarExtension).run {
            val errors = validate()

            if (errors.isNotEmpty()) {
                showError(errors.joinToString(System.lineSeparator()))
            } else {
                val outputPath = "${project.projectDir}/src/${variant}/kotlin/"
                val filePath = "${project.projectDir}/${filePath}"
                try {
                    println("Tracking plan file path: $filePath")
                    if (collarGenerator.generate(filePath, outputPath, packageName)) {
                        println("Tracking plan classes generated on path: $outputPath")
                    } else {
                        showError("Task generate failed")
                    }
                } catch (e: Exception) {
                    showError(e.stackTrace.toString())
                }
            }
        }
    }
}