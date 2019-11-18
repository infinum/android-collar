package co.infinum.collar.plugin.config

import co.infinum.collar.plugin.CollarExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.internal.api.dsl.extensions.BaseExtension2
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

private const val TAG = "Collar:"
private const val PLUGIN_EXCEPTION = "$TAG You must apply the Android plugin"

class Config(
    private val project: Project
) {

    private val baseExtension: BaseExtension
    val plugin: BasePlugin<out BaseExtension2>

    init {
        when {
            project.plugins.hasPlugin(AppPlugin::class.java) -> {
                baseExtension = project.extensions.getByType(AppExtension::class.java)
                plugin = project.plugins.getPlugin(AppPlugin::class.java)
            }
            else -> {
                throw GradleException(PLUGIN_EXCEPTION)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getBootClasspath(): List<File> =
        baseExtension.bootClasspath ?: plugin::class.java.getMethod("getRuntimeJarList").invoke(plugin) as List<File>

    fun collar(): CollarExtension = project.extensions.getByType(CollarExtension::class.java)
}