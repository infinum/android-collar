package co.infinum.processor.specs

import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.shared.Constants.CLASS_ACTIVITY
import co.infinum.processor.shared.Constants.CLASS_ANDROIDX_FRAGMENT
import co.infinum.processor.shared.Constants.CLASS_COMPONENT_ACTIVITY
import co.infinum.processor.shared.Constants.CLASS_FRAGMENT
import co.infinum.processor.shared.Constants.CLASS_SUPPORT_FRAGMENT
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File

class ScreenNameSpec private constructor(
    outputDir: File,
    private val packageName: String,
    private val simpleName: String,
    private val holders: Set<ScreenHolder>
) : Spec {

    companion object {
        private val CLASS_COLLAR = ClassName("co.infinum.collar", "Collar")

        private const val FUNCTION_NAME_TRACK_SCREEN = "trackScreen"

        private const val DEFAULT_PACKAGE_NAME = "co.infinum.collar"
        private const val DEFAULT_SIMPLE_NAME = "ScreenNames"
    }

    open class Builder(
        private var outputDir: File? = null,
        private var packageName: String = DEFAULT_PACKAGE_NAME,
        private var simpleName: String = DEFAULT_SIMPLE_NAME,
        private var holders: Set<ScreenHolder> = setOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun packageName(packageName: String) = apply { this.packageName = packageName }
        fun simpleName(simpleName: String) = apply { this.simpleName = simpleName }
        fun holders(holders: Set<ScreenHolder>) = apply { this.holders = holders }
        fun build() = ScreenNameSpec(outputDir!!, packageName, simpleName, holders)
    }

    init {
        build().writeTo(outputDir)
    }

    override fun file(): FileSpec =
        FileSpec.builder(packageName, simpleName)
            .addAnnotation(jvmName())
            .addComment(comment().toString())
            .build()

    override fun jvmName(): AnnotationSpec =
        AnnotationSpec.builder(JvmName::class.java)
            .addMember("%S", "${CLASS_COLLAR.simpleName}$simpleName")
            .build()

    override fun comment(): CodeBlock =
        CodeBlock.builder()
            .addStatement("Matches classes to screen names and logs it using [%T.%L].", CLASS_COLLAR, FUNCTION_NAME_TRACK_SCREEN)
            .addStatement("")
            .addStatement("This is a generated extension file. Do not edit.")
            .build()

    override fun build(): FileSpec =
        file().toBuilder().apply {
            val groupedHolders = holders.groupBy { it.superClassName }
            val cleanHolders = groupedHolders.keys.filterNotNull().map {
                it to groupedHolders[it].orEmpty()
            }.toMap()

            checkForDeprecation(this, cleanHolders)

            cleanHolders.mapNotNull { mapEntry ->
                val extensionFunSpecBuilder = FunSpec.builder(FUNCTION_NAME_TRACK_SCREEN)
                extensionFunSpecBuilder
                    .receiver(mapEntry.key)
                    .beginControlFlow("when (this) {")

                val codeBlockBuilder = CodeBlock.builder()
                    .indent()
                for (screenHolder in mapEntry.value) {
                    codeBlockBuilder.addStatement("is %T -> %S", screenHolder.className, screenHolder.screenName)
                }
                codeBlockBuilder.addStatement("else -> null")
                codeBlockBuilder.unindent()
                codeBlockBuilder.unindent()
                codeBlockBuilder.add("}?")

                extensionFunSpecBuilder.addCode(codeBlockBuilder.build())

                when (mapEntry.key) {
                    CLASS_COMPONENT_ACTIVITY -> {
                        extensionFunSpecBuilder.addStatement(
                            ".let { %T.%L(this, it) }",
                            CLASS_COLLAR,
                            FUNCTION_NAME_TRACK_SCREEN
                        )
                    }
                    CLASS_ANDROIDX_FRAGMENT -> {
                        extensionFunSpecBuilder.addStatement(
                            ".let { activity?.let { activity -> %T.%L(activity, it) } }",
                            CLASS_COLLAR,
                            FUNCTION_NAME_TRACK_SCREEN
                        )
                    }
                    CLASS_ACTIVITY -> {
                        extensionFunSpecBuilder.addStatement(
                            ".let { %T.%L(this, it) }",
                            CLASS_COLLAR,
                            FUNCTION_NAME_TRACK_SCREEN
                        )
                    }
                    else -> {
                        extensionFunSpecBuilder.addStatement(
                            ".let { activity?.let { activity -> %T.%L(activity, it) } }",
                            CLASS_COLLAR,
                            FUNCTION_NAME_TRACK_SCREEN
                        )
                    }
                }
                extensionFunSpecBuilder.build()
            }
                .forEach { addFunction(it) }
        }.build()

    private fun checkForDeprecation(fileSpec: FileSpec.Builder, holdersMap: Map<ClassName, List<ScreenHolder>>) =
        if (holdersMap
                .mapNotNull { it.key }
                .any { it == CLASS_SUPPORT_FRAGMENT || it == CLASS_FRAGMENT }
        ) {
            fileSpec.addAnnotation(AnnotationSpec.builder(Suppress::class.java).addMember(CodeBlock.of("%S", "DEPRECATION")).build())
        } else {
            fileSpec
        }

}

@DslMarker
annotation class ScreenNameSpecDsl

@ScreenNameSpecDsl
class ScreenNameSpecBuilder : ScreenNameSpec.Builder()

inline fun screenNameSpec(builder: ScreenNameSpecBuilder.() -> Unit): ScreenNameSpec {
    val specBuilder = ScreenNameSpecBuilder()
    specBuilder.builder()
    return specBuilder.build()
}