package co.infinum.collar.plugin

import co.infinum.collar.plugin.CollarConstants.COLLAR_EXTENSION
import co.infinum.collar.plugin.CollarConstants.COLLAR_GENERATE_TASK
import co.infinum.collar.plugin.CollarConstants.COLLAR_GENERATE_TASKS_GROUP
import co.infinum.collar.plugin.CollarConstants.COLLAR_GENERATE_TASK_DESC
import co.infinum.collar.plugin.tasks.GenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(COLLAR_EXTENSION, CollarExtension::class.java)

        addRepositories(project)
        addDependencies(project, extension)
        addProcessorDependecy(project, extension)

        project.tasks.create<GenerateTask>(COLLAR_GENERATE_TASK, GenerateTask::class.java).run {
            description = COLLAR_GENERATE_TASK_DESC
            group = COLLAR_GENERATE_TASKS_GROUP
        }
    }

    private fun addRepositories(project: Project) {
        with(project.repositories) {
            google()
            jcenter()
            mavenCentral()
            maven {}.url = URI("http://dl.bintray.com/infinum/android")
        }
    }

    private fun addDependencies(project: Project, settings: CollarExtension) {
        project.dependencies.add("implementation", "co.infinum.collar:collar-annotations:${settings.version}")
        project.dependencies.add("implementation", "co.infinum.collar:collar-core:${settings.version}")

        if (project.pluginManager.hasPlugin("kotlin-android")) {
            if (project.pluginManager.hasPlugin("kotlin-kapt").not()) {
                project.pluginManager.apply("kotlin-kapt")
            }
            project.dependencies.add("kapt", "co.infinum.collar:collar-processor:${settings.version}")
        } else {
            project.dependencies.add("annotationProcessor", "co.infinum.collar:collar-processor:${settings.version}")
        }
    }
}