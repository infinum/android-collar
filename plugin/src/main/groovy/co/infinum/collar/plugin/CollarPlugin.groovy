package co.infinum.collar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CollarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.dependencies {
            implementation "org.aspectj:aspectjrt:1.9.4"
            implementation 'co.infinum.collar:collar-core:1.0.0'
        }

        project.android.applicationVariants.all { variant ->
            def variantName = variant.name[0].toUpperCase() + variant.name[1..-1].toLowerCase()
            project.tasks.findByName('compile' + variantName + 'Sources')?.doLast {

            }
            project.tasks.findByName('compile' + variantName + 'UnitTestSources')?.doLast {

            }
        }
    }
}