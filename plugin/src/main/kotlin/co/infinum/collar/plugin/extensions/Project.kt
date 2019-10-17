package co.infinum.collar.plugin.extensions

import org.gradle.api.Project

inline fun <reified T> Project.whenEvaluated(noinline fn: Project.() -> T) {
    if (state.executed) fn() else afterEvaluate { fn.invoke(this) }
}