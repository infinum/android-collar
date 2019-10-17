package co.infinum.collar.plugin.aspectj

import co.infinum.collar.plugin.logger.logBuildParametersAdapted
import co.infinum.collar.plugin.logger.logExtraAjcArgumentAlreadyExists
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File
import java.util.*

internal class AspectJWeaver(private val project: Project) {

    private val errorReminder = "Look into %s file for details"

    var compilationLogFile: String? = null
        internal set(name) {
            if (name.isNullOrBlank().not()) {
                field = project.buildDir.absolutePath + File.separator + name
            }
        }

    var transformLogFile: String? = null
        internal set(name) {
            if (name.isNullOrBlank().not()) {
                field = project.buildDir.absolutePath + File.separator + name
            }
        }

    var encoding: String? = null
    var weaveInfo: Boolean = false
    var debugInfo: Boolean = false
    var ignoreErrors: Boolean = false
    var breakOnError: Boolean = false
    var ajcArgs = LinkedHashSet<String>()

    @Suppress("SetterBackingFieldAssignment")
    var ajSources: MutableSet<File> = LinkedHashSet()
        internal set(ajSources) {
            ajSources.forEach { field.add(it) }
        }

    var aspectPath: MutableSet<File> = LinkedHashSet()
    var inPath: MutableSet<File> = LinkedHashSet()
    var classPath: MutableSet<File> = LinkedHashSet()
    var bootClasspath: String? = null
    var sourceCompatibility: String? = null
    var targetCompatibility: String? = null
    var destinationDir: String? = null

    internal fun doWeave() {
        val log = prepareLogger()

        // http://www.eclipse.org/aspectj/doc/released/devguide/ajc-ref.html

        val args = mutableListOf(
            "-encoding", encoding,
            "-source", sourceCompatibility,
            "-target", targetCompatibility,
            "-d", destinationDir,
            "-bootclasspath", bootClasspath,
            "-classpath", classPath.joinToString(separator = File.pathSeparator)
        )

        if (ajSources.isNotEmpty()) {
            args + "-sourceroots" + ajSources.joinToString(separator = File.pathSeparator)
        }

        if (inPath.isNotEmpty()) {
            args + "-inpath" + inPath.joinToString(separator = File.pathSeparator)
        }

        if (aspectPath.isNotEmpty()) {
            args + "-aspectpath" + aspectPath.joinToString(separator = File.pathSeparator)
//        } else {
//            aspectPath = mutableSetOf(File("/Users/bojan/.gradle/caches/transforms-2/files-2.1/d32a188cbf0f672150ce46f5f2d0a018/collar-core-1.0.0/jars/classes.jar\n"))
//            args + "-aspectpath" + aspectPath.joinToString(separator = File.pathSeparator)
        }
        println("BOJAN: $aspectPath")

        if (getLogFile().isNotBlank()) {
            args + "-log" + getLogFile()
        }

        if (debugInfo) {
            args + "-g"
        }

        if (weaveInfo) {
            args + "-showWeaveInfo"
        }

        if (ignoreErrors) {
            args + "-proceedOnError" + "-noImportError"
        }

        if (ajcArgs.isNotEmpty()) {
            ajcArgs.forEach { extra ->
                if (extra.startsWith('-') && args.contains(extra)) {
                    logExtraAjcArgumentAlreadyExists(extra)
                    log.writeText("[warning] Duplicate argument found while composing ajc config! Build may be corrupted.\n\n")
                }
                args + extra
            }
        }

        log.writeText("Full Collar AjC build args: ${args.joinToString()}\n\n")
        logBuildParametersAdapted(args, log.name)

        val handler = MessageHandler(true)
        Main().run(args.toTypedArray(), handler)
        for (message in handler.getMessages(null, true)) {
            when (message.kind) {
                IMessage.ERROR -> {
                    log.writeText("[error]" + message?.message + "${message?.thrown}\n\n")
                    if (breakOnError) throw GradleException (errorReminder.format(getLogFile()))
                }
                IMessage.FAIL, IMessage.ABORT -> {
                    log.writeText("[error]" + message?.message + "${message?.thrown}\n\n")
                    throw GradleException (message.message)
                }
                IMessage.INFO, IMessage.DEBUG, IMessage.WARNING -> {
                    log.writeText("[warning]" + message?.message + "${message?.thrown}\n\n")
                    if (getLogFile().isNotBlank()) log.writeText("${errorReminder.format(getLogFile())}\n\n")
                }
            }
        }

        detectErrors()
    }

    private fun getLogFile(): String = compilationLogFile ?: transformLogFile.orEmpty()

    private fun prepareLogger(): File {
        val lf = project.file(getLogFile())
        if (lf.exists()) {
            lf.delete()
        }

        return lf
    }

    private fun detectErrors() {
        val lf: File  = project.file(getLogFile())
        if (lf.exists()) {
            lf.readLines().reversed().forEach { line ->
                if (line.contains("[error]") && breakOnError) {
                    throw GradleException ("$line\n${errorReminder.format(getLogFile())}")
                }
            }
        }
    }

    private inline operator fun <reified E> MutableCollection<in E>.plus(elem: E): MutableCollection<in E> {
        this.add(elem)
        return this
    }
}