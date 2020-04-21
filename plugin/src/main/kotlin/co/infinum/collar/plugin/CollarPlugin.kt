package co.infinum.collar.plugin

import co.infinum.collar.plugin.tasks.GenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project) =
        with(project) {
            addRepositories(this)
            addDependencies(this)
            addTasks(this)
        }

    private fun addRepositories(project: Project) =
        with(project.repositories) {
            jcenter()
            mavenCentral()
            maven {}.url = URI("https://dl.bintray.com/infinum/android")
        }

    private fun addDependencies(project: Project) {
        with(project) {
            val settings = extensions.create(CollarExtension.NAME, CollarExtension::class.java)

            dependencies.add("implementation", "co.infinum.collar:collar-core:${settings.version}")

            if (pluginManager.hasPlugin("kotlin-android")) {
                if (pluginManager.hasPlugin("kotlin-kapt").not()) {
                    pluginManager.apply("kotlin-kapt")
                }
                dependencies.add("kapt", "co.infinum.collar:collar-processor:${settings.version}")
            } else {
                dependencies.add("annotationProcessor", "co.infinum.collar:collar-processor:${settings.version}")
            }
        }
    }

    private fun addTasks(project: Project) {
        with(project) {
            tasks.create(GenerateTask.NAME, GenerateTask::class.java).run {
                group = GenerateTask.GROUP
                description = GenerateTask.DESCRIPTION
            }
        }
    }
}
