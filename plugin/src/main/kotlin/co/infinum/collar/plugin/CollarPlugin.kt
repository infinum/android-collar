package co.infinum.collar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("collar", CollarExtension::class.java)

        addRepositories(project)
        addDependencies(project, extension)
        addProcessorDependecy(project, extension)
    }

    private fun addRepositories(project: Project) {
        with(project.repositories) {
            google()
            jcenter()
            mavenCentral()
            maven {}.url = URI("https://dl.bintray.com/knobtviker/maven")
            maven {}.url = URI("http://dl.bintray.com/infinum/android")
        }
    }

    private fun addDependencies(project: Project, settings: CollarExtension) {
        with(project.dependencies) {
            if (settings.extended) {
                add("implementation", "androidx.core:core-ktx:1.1.0")
            }
            add("implementation", "co.infinum.collar:collar-core:${settings.version}")
            add("implementation", "co.infinum.collar:collar-annotations:${settings.version}")
        }
    }

    private fun addProcessorDependecy(project: Project, settings: CollarExtension) {
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