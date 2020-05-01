package co.infinum.processor.subprocessors

import co.infinum.processor.configurations.Configuration
import javax.annotation.processing.RoundEnvironment

internal interface Subprocessor {

    fun init(configuration: Configuration)

    fun process(roundEnvironment: RoundEnvironment)
}
