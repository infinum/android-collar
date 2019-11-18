package co.infinum.collar.plugin

import co.infinum.collar.plugin.config.Config
import co.infinum.collar.plugin.extensions.whenEvaluated
import co.infinum.collar.plugin.listeners.BuildTimeListener
import co.infinum.collar.plugin.utils.javaTask
import co.infinum.collar.plugin.utils.variantDataList
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val config = Config(project)
        val extension = project.extensions.create("collar", CollarExtension::class.java)

        addDependencies(project, extension)

        project.whenEvaluated {
            configureCompiler(project, config)
            if (extension.buildTimeLog) {
                project.gradle.addListener(BuildTimeListener())
            }
        }
    }

    private fun addDependencies(project: Project, settings: CollarExtension) {
        with(project) {
            with(repositories) {
                jcenter()
                mavenCentral()
                maven {}.url = URI("https://dl.bintray.com/knobtviker/maven")
                maven {}.url = URI("http://dl.bintray.com/infinum/android")
            }
            with(dependencies) {
                add("implementation", "org.aspectj:aspectjrt:${settings.ajc}")
                add("implementation", "co.infinum.collar:collar-core:1.0.0")
            }
        }
    }

    private fun configureCompiler(project: Project, config: Config) {
        variantDataList(config.plugin).forEach { variant ->
            val variantName = variant.name.capitalize()

            val taskName = "compile${variantName}Collar"

            CollarTask.Builder(project)
                .plugin(project.plugins.getPlugin(CollarPlugin::class.java))
                .extension(project.extensions.getByType(CollarExtension::class.java))
                .compiler(javaTask(variant))
                .variant(variant.name)
                .name(taskName)
                .buildAndAttach(config)
        }
    }
}