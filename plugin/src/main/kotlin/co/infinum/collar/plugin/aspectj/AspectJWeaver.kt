package co.infinum.collar.plugin.aspectj

import co.infinum.collar.plugin.logger.logBuildParametersAdapted
import co.infinum.collar.plugin.logger.logExtraAjcArgumentAlreadyExists
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project
import java.io.File
import java.util.LinkedHashSet

class AspectJWeaver(
    private val project: Project
) {
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
    var ajcArgs = LinkedHashSet<String>()

    var aspectPath: MutableSet<File> = LinkedHashSet()
    var inPath: MutableSet<File> = LinkedHashSet()
    var classPath: MutableSet<File> = LinkedHashSet()
    var bootClasspath: String? = null
    var sourceCompatibility: String? = null
    var targetCompatibility: String? = null
    var destinationDir: String? = null

    fun weave() {
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

        if (inPath.isNotEmpty()) {
            args.add("-inpath")
            args.add(inPath.joinToString(separator = File.pathSeparator))
        } else {
            println("VANJA inPath IS EMPTY!!!")
        }

        if (aspectPath.isNotEmpty()) {
            args.add("-aspectpath")
            args.add(aspectPath.joinToString(separator = File.pathSeparator))
        } else {
            println("ĐANI aspectPath IS EMPTY!!!")
        }

        if (logFile().isNotBlank()) {
            args + "-log" + logFile()
        }

        if (debugInfo) {
            args + "-g"
        }

        if (weaveInfo) {
            args + "-showWeaveInfo"
        }

        if (ajcArgs.isNotEmpty()) {
            ajcArgs.forEach { extra ->
                if (extra.startsWith('-') && args.contains(extra)) {
                    logExtraAjcArgumentAlreadyExists(extra)
                    log.writeText("[warning] Duplicate argument found while composing ajc extension! Build may be corrupted.\n\n")
                }
                args + extra
            }
        }

        logBuildParametersAdapted(args, log.name)

        val handler = MessageHandler(true)
        Main().run(args.toTypedArray(), handler)

        for (message in handler.getMessages(null, true)) {
            when (message.kind) {
                IMessage.ERROR, IMessage.FAIL, IMessage.ABORT -> {
                    project.logger.error(message?.message, message?.thrown)
                    log.writeText("[error] " + message?.message + " ${message?.thrown} " + message?.sourceLocation?.sourceFileName + "\n\n")
                }
                IMessage.INFO -> {
                    project.logger.info(message?.message, message?.thrown)
                    log.writeText("[info] " + message?.message + " ${message?.thrown} " + message?.sourceLocation?.sourceFileName + "\n\n")
                }
                IMessage.DEBUG -> {
                    project.logger.debug(message?.message, message?.thrown)
                    log.writeText("[debug] " + message?.message + " ${message?.thrown} " + message?.sourceLocation?.sourceFileName + "\n\n")
                }
                IMessage.WARNING -> {
                    project.logger.warn(message?.message, message?.thrown)
                    log.writeText("[warning] " + message?.message + " ${message?.thrown} " + message?.sourceLocation?.sourceFileName + "\n\n")
                }
            }
        }
    }

    private fun logFile(): String = compilationLogFile ?: transformLogFile.orEmpty()

    private fun prepareLogger(): File {
        val logFile = project.file(logFile())
        if (logFile.exists()) {
            logFile.delete()
        }

        return logFile
    }
}