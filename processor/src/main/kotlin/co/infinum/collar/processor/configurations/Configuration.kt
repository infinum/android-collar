package co.infinum.collar.processor.configurations

import co.infinum.collar.processor.options.Options
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal interface Configuration {

    fun messager(): Messager

    fun outputDir(): File?

    fun options(): Options

    fun elementUtils(): Elements

    fun typeUtils(): Types
}
