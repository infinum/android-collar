package co.infinum.collar.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CollarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.dependencies {
            classpath "com.archinamon:android-gradle-aspectj:3.4.0"
            implementation "co.infinum.collar:collar-core:1.0.0"
        }
        project.plugins.apply("com.archinamon.aspectj")

        project.android.applicationVariants.all { variant ->
            // Gets the variant name and capitalize the first character
            def variantName = variant.name[0].toUpperCase() + variant.name[1..-1].toLowerCase()

            // Weave the binary for the actual code
            // CompileSources task is invoked after java and kotlin compilers and copy kotlin classes
            // That's the moment we have the finalized byte code and we can weave the aspects
            project.tasks.findByName('compile' + variantName + 'Sources')?.doLast {
                project.tasks.getByName('transformClassesWithAspectjFor'+ variantName)
            }

            // Weave the binary for unit tests
            // compile unit tests task is invoked after the byte code is finalized
            // This is the time that we can weave the aspects onto byte code
            project.tasks.findByName('compile' + variantName + 'UnitTestSources')?.doLast {
                project.tasks.getByName('transformClassesWithAspectjFor'+ variantName+ 'AndroidTest')
            }
        }
    }
}