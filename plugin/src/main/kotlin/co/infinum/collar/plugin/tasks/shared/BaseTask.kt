package co.infinum.collar.plugin.tasks.shared

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

internal abstract class BaseTask : DefaultTask() {

    internal fun showError(message: String): Nothing = throw TaskExecutionException(this, Exception(message))
}
