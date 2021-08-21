package com.infinum.collar.plugin

import com.android.build.gradle.AppExtension
import com.infinum.collar.plugin.tasks.GenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project

public class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit =
        with(project) {
            addExtension(this)
            addRepositories(this)
            addDependencies(this)
            addTasks(this)
        }

    private fun addExtension(project: Project) =
        project.extensions.create(CollarExtension.NAME, CollarExtension::class.java)

    private fun addRepositories(project: Project) =
        with(project.repositories) {
            mavenCentral()
        }

    private fun addDependencies(project: Project) {
        with(project) {
            dependencies.add("implementation", "com.infinum.collar:collar-annotations:$collarVersion")
            dependencies.add("implementation", "com.infinum.collar:collar-core:$collarVersion")

            if (pluginManager.hasPlugin("kotlin-android")) {
                if (pluginManager.hasPlugin("kotlin-kapt").not()) {
                    pluginManager.apply("kotlin-kapt")
                }
                dependencies.add("kapt", "com.infinum.collar:collar-processor:$collarVersion")
            } else {
                dependencies.add("annotationProcessor", "com.infinum.collar:collar-processor:$collarVersion")
            }
        }
    }

    private fun addTasks(project: Project) {
        with(project) {
            (extensions.findByName(CollarExtension.NAME) as? CollarExtension)?.let { collarExtension ->
                tasks.register(GenerateTask.NAME, GenerateTask::class.java) { task ->
                    task.group = GenerateTask.GROUP
                    task.description = GenerateTask.DESCRIPTION
                    task.setSource(projectDir)
                    task.include {
                        it.name == collarExtension.fileName
                    }
                }.let {
                    extensions.findByType(AppExtension::class.java)?.applicationVariants?.all { variant ->
                        variant.registerJavaGeneratingTask(it.get(), it.get().outputDirectory)
                    }
                }
            }
        }
    }
}
