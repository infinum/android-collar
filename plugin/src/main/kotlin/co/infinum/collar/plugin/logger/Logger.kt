package co.infinum.collar.plugin.logger

import java.io.File

internal fun logCompilationStart() {
    println("---------- Starting Collar sources compilation ----------")
}

internal fun logCompilationFinish() {
    println("---------- Finish Collar compiler ----------")
}

internal fun logAugmentationStart() {
    println("---------- Starting augmentation with Collar transformer ----------")
}

internal fun logAugmentationFinish() {
    println("---------- Finish Collar transformer ----------")
}

internal fun logNoAugmentation() {
    println("---------- Exit Collar transformer w/o processing ----------")
}

internal fun logJarAspectAdded(file: File) {
    println("include aspects from :: ${file.absolutePath}")
}

internal fun logExtraAjcArgumentAlreadyExists(arg: String) {
    println("extra AjC argument $arg already exists in build config")
}

internal fun logBuildParametersAdapted(args: MutableCollection<String?>, logfile: String) {
    fun extractParamsToString(it: String): String {
        return when {
            it.startsWith('-') -> "$it :: "
            else -> "$it, \n"
//            else -> when {
//                it.length > 200 -> "[ list files ],\n"
//                else -> "$it, "
//            }
        }
    }

    val params = args
        .filterNotNull()
        .joinToString(transform = ::extractParamsToString)

    println("Collar config: $params\n")
    println("Detailed log in $logfile")
}
