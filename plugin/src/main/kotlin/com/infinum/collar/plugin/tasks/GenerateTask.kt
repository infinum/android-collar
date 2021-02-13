package com.infinum.collar.plugin.tasks

import com.android.builder.model.AndroidProject.FD_GENERATED
import com.infinum.collar.generator.CollarGenerator
import com.infinum.collar.plugin.CollarExtension
import com.infinum.collar.plugin.tasks.shared.BaseSourceTask
import com.infinum.collar.plugin.validate
import java.io.File
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

@CacheableTask
internal open class GenerateTask : BaseSourceTask() {

    companion object {
        const val GROUP = "collar"
        const val NAME = "generate"
        const val DESCRIPTION = "Generates Kotlin files for screen names, events and user properties."
    }

    private val collarGenerator = CollarGenerator()

    @Suppress("unused") // Required to invalidate the task on version updates.
    @Input
    private val pluginVersion = CollarExtension.DEFAULT_VERSION

    @InputFiles
    @SkipWhenEmpty
    @PathSensitive(PathSensitivity.ABSOLUTE)
    override fun getSource(): FileTree =
        super.getSource()

    @get:OutputDirectory
    var outputDirectory: File = File(
        project.buildDir,
        "$FD_GENERATED${File.separatorChar}source${File.separatorChar}${CollarExtension.NAME}"
    )

    @TaskAction
    fun doOnRun() {
        (project.extensions.findByName(CollarExtension.NAME) as CollarExtension).run {
            val errors = validate()
            when {
                errors.isNotEmpty() -> showError(errors.joinToString(System.lineSeparator()))
                else -> {
                    generateTrackingPlan(packageName)
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun generateTrackingPlan(packageName: String) {
        try {
            println("Tracking plan file path: ${source.first().absolutePath}")
            if (collarGenerator.generate(source.first().absolutePath, outputDirectory.absolutePath, packageName)) {
                println("Tracking plan classes generated on path: ${outputDirectory.absolutePath}")
            } else {
                showError("Task generate failed")
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}
