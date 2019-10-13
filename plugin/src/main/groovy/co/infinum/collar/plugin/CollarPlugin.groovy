package co.infinum.collar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CollarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.dependencies {
            implementation 'co.infinum.collar:collar-core:1.0.0'
        }
        project.getPluginManager().apply("com.archinamon.aspectj")
        println("PLUGIN APPLIED - BOJAN IS AWESOME!!!3")
        project.extensions.getByType(AspectJExtension.class)['includeAspectsFromJar'] = 'core'
        println("PLUGIN SETUP - BOJAN IS AWESOME!!!4")

        project.android.applicationVariants.all { variant ->
            def variantName = variant.name[0].toUpperCase() + variant.name[1..-1].toLowerCase()
            project.tasks.findByName('compile' + variantName + 'Sources')?.doLast {
                project.tasks.getByName('transformClassesWithAspectjFor' + variantName)
            }
            project.tasks.findByName('compile' + variantName + 'UnitTestSources')?.doLast {
                project.tasks.getByName('transformClassesWithAspectjFor' + variantName + 'AndroidTest')
            }
        }
    }
}