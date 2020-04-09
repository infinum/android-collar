package co.infinum.processor.configurations

import co.infinum.processor.options.Options
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

interface Configuration {

    fun messager(): Messager

    fun outputDir(): File?

    fun options(): Options

    fun elementUtils(): Elements

    fun typeUtils(): Types
}