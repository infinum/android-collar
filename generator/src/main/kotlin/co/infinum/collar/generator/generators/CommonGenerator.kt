package co.infinum.collar.generator.generators

import com.squareup.kotlinpoet.FileSpec
import java.nio.file.Paths

abstract class CommonGenerator(
    private val outputPath: String,
    private val packageName: String,
    private val className: String
) : Generator {

    companion object {
        internal const val ANNOTATION_FORMAT = "value = %S"
    }

    override fun file(): FileSpec.Builder = FileSpec.builder(packageName, className)

    override fun write(fileSpec: FileSpec) = fileSpec.writeTo(Paths.get(outputPath))

    override fun generate(): Boolean {
        write(
            file()
                .addType(type())
                .build()
        )
        return true
    }
}