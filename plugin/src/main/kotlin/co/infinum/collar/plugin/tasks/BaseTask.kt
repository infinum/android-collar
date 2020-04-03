package co.infinum.collar.plugin.tasks

import co.infinum.collar.generator.GeneratorLib
import org.gradle.api.DefaultTask

open class BaseTask : DefaultTask() {

    val generatorLib = GeneratorLib()
}