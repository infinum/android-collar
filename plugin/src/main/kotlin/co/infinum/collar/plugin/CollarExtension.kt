package co.infinum.collar.plugin

import org.gradle.api.JavaVersion

open class CollarExtension {

    open var ajc = "1.9.4"
    open var java = JavaVersion.VERSION_1_7

    open var ajcArgs = mutableSetOf<String>()

    open var weaveInfo = true
    open var debugInfo = false

    open var ignoreErrors = false
    open var breakOnError = true

    open var buildTimeLog = true

    open var transformLogFile = "collar-transform.log"
    open var compilationLogFile = "collar-compile.log"

    fun ajcArgs(vararg args: String): CollarExtension {
        ajcArgs.addAll(args)
        return this
    }
}
