package co.infinum.collar.plugin

import co.infinum.collar.plugin.aspectj.AspectJTransform
import co.infinum.collar.plugin.config.AndroidConfig
import co.infinum.collar.plugin.extensions.whenEvaluated
import co.infinum.collar.plugin.listeners.BuildTimeListener
import co.infinum.collar.plugin.utils.javaTask
import co.infinum.collar.plugin.utils.variantDataList
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class CollarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val config = AndroidConfig(project)
        val settings = project.extensions.create("collar", CollarExtension::class.java)

        configProject(project, config, settings)

        val transformer = AspectJTransform(project)
        val module = project.extensions.getByType(AppExtension::class.java)

        transformer.withConfig(config).prepareProject()
        module.registerTransform(transformer)
    }

    private fun configProject(project: Project, config: AndroidConfig, settings: CollarExtension) {
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

            whenEvaluated {
                configureCompiler(project, config)
                if (settings.buildTimeLog) {
                    project.gradle.addListener(BuildTimeListener())
                }
            }
        }
    }

    private fun configureCompiler(project: Project, config: AndroidConfig) {
        variantDataList(config.plugin).forEach variantScanner@{ variant ->
            val variantName = variant.name.capitalize()

            val taskName = "compile${variantName}Collar"
            val ajc = CollarTask.Builder(project)
                .plugin(project.plugins.getPlugin(CollarPlugin::class.java))
                .config(project.extensions.getByType(CollarExtension::class.java))
                .compiler(javaTask(variant))
                .variant(variant.name)
                .name(taskName)

            ajc.buildAndAttach(config)
        }
    }
}