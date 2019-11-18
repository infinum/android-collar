package co.infinum.collar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("collar", CollarExtension::class.java)
        addDependencies(project, extension)
    }

    private fun addDependencies(project: Project, settings: CollarExtension) {
        with(project) {
            with(repositories) {
                google()
                jcenter()
                mavenCentral()
                maven {}.url = URI("https://dl.bintray.com/knobtviker/maven")
                maven {}.url = URI("http://dl.bintray.com/infinum/android")
            }
            with(dependencies) {
                add("implementation", "androidx.core:core-ktx:1.1.0")
                add("implementation", "co.infinum.collar:collar-core:" + settings.version)
            }
        }
    }
}