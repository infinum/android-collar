package co.infinum.collar.plugin

import co.infinum.collar.plugin.aspectj.AspectJWeaver
import co.infinum.collar.plugin.config.Config
import co.infinum.collar.plugin.extensions.append
import co.infinum.collar.plugin.extensions.appendAll
import co.infinum.collar.plugin.logger.logCompilationFinish
import co.infinum.collar.plugin.logger.logCompilationStart
import co.infinum.collar.plugin.logger.logJarAspectAdded
import co.infinum.collar.plugin.utils.ClasspathFileCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.compile.JavaCompile
import java.io.File
import java.util.*

internal open class CollarTask : AbstractCompile() {

    lateinit var aspectJWeaver: AspectJWeaver

    @TaskAction
    override fun compile() {
        logCompilationStart()

        destinationDir.deleteRecursively()

        aspectJWeaver.classPath = LinkedHashSet(classpath.files)
        aspectJWeaver.doWeave()

        logCompilationFinish()
    }

    internal class Builder(private val project: Project) {

        private lateinit var plugin: Plugin<Project>
        private lateinit var config: CollarExtension
        private lateinit var javaCompiler: JavaCompile
        private lateinit var variantName: String
        private lateinit var taskName: String

        fun plugin(plugin: Plugin<Project>): Builder {
            this.plugin = plugin
            return this
        }

        fun config(extension: CollarExtension): Builder {
            this.config = extension
            return this
        }

        fun compiler(compiler: JavaCompile): Builder {
            this.javaCompiler = compiler
            return this
        }

        fun variant(name: String): Builder {
            this.variantName = name
            return this
        }

        fun name(name: String): Builder {
            this.taskName = name
            return this
        }

        fun buildAndAttach(android: Config) {
            val options = mutableMapOf(
                "overwrite" to true,
                "dependsOn" to javaCompiler.name,
                "group" to "build",
                "description" to "Compile .aj source files into java .class with meta instructions",
                "type" to CollarTask::class.java
            )

            val task = project.task(options, taskName) as CollarTask
            with(task) {
                destinationDir = javaCompiler.destinationDir
                aspectJWeaver = AspectJWeaver(project).apply {
                    inPath append this@with.destinationDir

                    targetCompatibility = config.java.toString()
                    sourceCompatibility = config.java.toString()
                    destinationDir = this@with.destinationDir.absolutePath
                    bootClasspath = android.getBootClasspath().joinToString(separator = File.pathSeparator)
                    encoding = javaCompiler.options.encoding

                    compilationLogFile = config.compilationLogFile
                    debugInfo = config.debugInfo
                    ignoreErrors = config.ignoreErrors
                    breakOnError = config.breakOnError
                    ajcArgs appendAll config.ajcArgs
                }

                classpath = classpath()
                doFirst { classpath += javaCompiler.classpath }

                findCompiledAspectsInClasspath(this@with)
            }

            // javaCompile.classpath does not contain exploded-aar/**/jars/*.jars till first run
            javaCompiler.doLast {
                task.classpath = classpath()
                findCompiledAspectsInClasspath(task)
            }

            // finally apply behavior
            javaCompiler.finalizedBy(task)
        }

        private fun classpath(): FileCollection = ClasspathFileCollection(setOf(javaCompiler.destinationDir).plus(javaCompiler.classpath))

        private fun findCompiledAspectsInClasspath(task: CollarTask) {
            val aspects: MutableSet<File> = mutableSetOf()

            javaCompiler.classpath
                .filter { it.absolutePath.contains("collar-core") }
                .forEach { file ->
                    logJarAspectAdded(file)
                    aspects.add(file)
                }

            task.aspectJWeaver.aspectPath appendAll aspects
        }
    }
}
