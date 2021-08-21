package com.infinum.collar.processor.subprocessors

import com.infinum.collar.processor.configurations.Configuration
import javax.annotation.processing.RoundEnvironment

internal interface Subprocessor {

    fun init(configuration: Configuration)

    fun process(roundEnvironment: RoundEnvironment)
}
