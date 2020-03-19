package co.infinum.processor.configurations

import co.infinum.processor.options.Options
import java.io.File
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

interface Configuration {

    fun outputDir(): File?

    fun options(): Options

    fun elementUtils(): Elements

    fun typeUtils(): Types
}