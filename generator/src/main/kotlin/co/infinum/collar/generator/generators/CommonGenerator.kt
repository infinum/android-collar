package co.infinum.collar.generator.generators

import com.squareup.kotlinpoet.FileSpec
import java.nio.file.Paths

internal abstract class CommonGenerator(
    private val outputPath: String,
    private val packageName: String,
    private val className: String
) : Generator {

    companion object {
        internal const val ANNOTATION_FORMAT = "value = %S"
        internal const val DEFAULT_INDENT = "    "
    }

    override fun file(): FileSpec.Builder = FileSpec.builder(packageName, className).indent(DEFAULT_INDENT)

    override fun write(fileSpec: FileSpec) = fileSpec.writeTo(Paths.get(outputPath))

    override fun generate(): Boolean {
        type()?.let {
            write(
                file()
                    .addType(it)
                    .build()
            )
        }
        return true
    }
}
