package com.infinum.collar.plugin.tasks.shared

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskExecutionException

@CacheableTask
internal abstract class BaseSourceTask : SourceTask() {
    internal fun showError(message: String): Nothing = throw TaskExecutionException(this, Exception(message))
}
