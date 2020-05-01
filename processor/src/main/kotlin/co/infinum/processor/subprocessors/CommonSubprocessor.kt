package co.infinum.processor.subprocessors

import co.infinum.processor.configurations.Configuration
import co.infinum.processor.options.Options
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal abstract class CommonSubprocessor : Subprocessor {

    internal lateinit var messager: Messager
    internal var generatedDir: File? = null
    internal lateinit var processorOptions: Options
    internal lateinit var elementUtils: Elements
    internal lateinit var typeUtils: Types

    override fun init(configuration: Configuration) =
        with(configuration) {
            messager = messager()
            generatedDir = outputDir()
            processorOptions = options()
            elementUtils = elementUtils()
            typeUtils = typeUtils()
        }
}
