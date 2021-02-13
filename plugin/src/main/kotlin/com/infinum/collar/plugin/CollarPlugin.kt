package com.infinum.collar.plugin

import com.android.build.gradle.AppExtension
import com.infinum.collar.plugin.tasks.GenerateTask
import java.net.URI
import org.gradle.api.Plugin
import org.gradle.api.Project

public class CollarPlugin : Plugin<Project> {

    public companion object {
        public const val VERSION: String = "1.2.3"
    }

    override fun apply(project: Project): Unit =
        with(project) {
            addRepositories(this)
            addDependencies(this)
            addTasks(this)
        }

    private fun addRepositories(project: Project) =
        with(project.repositories) {
            mavenCentral()
            maven {}.url = URI("https://dl.bintray.com/infinum/android")
        }

    private fun addDependencies(project: Project) {
        with(project) {
            dependencies.add("implementation", "com.infinum.collar:collar-annotations:$VERSION")
            dependencies.add("implementation", "com.infinum.collar:collar-core:$VERSION")

            if (pluginManager.hasPlugin("kotlin-android")) {
                if (pluginManager.hasPlugin("kotlin-kapt").not()) {
                    pluginManager.apply("kotlin-kapt")
                }
                dependencies.add("kapt", "com.infinum.collar:collar-processor:$VERSION")
            } else {
                dependencies.add("annotationProcessor", "com.infinum.collar:collar-processor:$VERSION")
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
