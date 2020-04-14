package co.infinum.processor.subprocessors

import co.infinum.processor.configurations.Configuration
import co.infinum.processor.options.Options
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

interface Subprocessor {

    fun init(configuration: Configuration)

    fun process(roundEnvironment: RoundEnvironment)
}