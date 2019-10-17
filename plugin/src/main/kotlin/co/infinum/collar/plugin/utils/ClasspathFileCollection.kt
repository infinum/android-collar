package co.infinum.collar.plugin.utils

import org.gradle.api.internal.file.AbstractFileCollection
import org.gradle.api.internal.tasks.TaskDependencyInternal
import org.gradle.api.tasks.TaskDependency
import java.io.File

class ClasspathFileCollection(
        private val files: Set<File>
) : AbstractFileCollection() {

    override fun getFiles() = files.toMutableSet()

    override fun getBuildDependencies(): TaskDependency = TaskDependencyInternal.EMPTY

    override fun getDisplayName() = DISPLAY_NAME

    private companion object {
        const val DISPLAY_NAME = "collarClasspath"
    }
}