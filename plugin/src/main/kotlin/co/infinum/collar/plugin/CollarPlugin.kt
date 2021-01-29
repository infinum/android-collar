package co.infinum.collar.plugin

import co.infinum.collar.plugin.tasks.GenerateTask
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

public class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit =
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

            dependencies.add("implementation", "co.infinum.collar:collar-annotations:${settings.version}")
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
            tasks.register(GenerateTask.NAME, GenerateTask::class.java) { task ->
                task.group = GenerateTask.GROUP
                task.description = GenerateTask.DESCRIPTION
                task.setSource(projectDir)
                task.include {
                    it.name == (extensions.findByName(CollarExtension.NAME) as CollarExtension).fileName
                }
            }.let {
                extensions.findByType(AppExtension::class.java)?.applicationVariants?.all { variant ->
                    variant.registerJavaGeneratingTask(it.get(), it.get().outputDirectory)
                }
            }
        }
    }
}
