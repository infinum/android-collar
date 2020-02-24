package co.infinum.processor.specs

import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.validators.TypeElementValidator
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
    private val holders: List<ScreenHolder>,
    private val typeElementValidator: TypeElementValidator
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
        private var holders: List<ScreenHolder> = listOf(),
        private var typeElementValidator: TypeElementValidator? = null
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun packageName(packageName: String) = apply { this.packageName = packageName }
        fun simpleName(simpleName: String) = apply { this.simpleName = simpleName }
        fun holders(holders: List<ScreenHolder>) = apply { this.holders = holders }
        fun typeElementValidator(typeElementValidator: TypeElementValidator) = apply { this.typeElementValidator = typeElementValidator }
        fun build() = ScreenNameSpec(outputDir!!, packageName, simpleName, holders, typeElementValidator!!)
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
            holders.groupBy { it.typeElement }
                .mapNotNull { mapEntry ->
                    typeElementValidator.resolve(mapEntry.key)?.let {
                        val extensionFunSpecBuilder = FunSpec.builder(FUNCTION_NAME_TRACK_SCREEN)

                            .receiver(it)
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

                        when (it) {
                            TypeElementValidator.CLASS_COMPONENT_ACTIVITY -> {
                                extensionFunSpecBuilder.addStatement(
                                    ".let { %T.%L(this, it) }",
                                    CLASS_COLLAR,
                                    FUNCTION_NAME_TRACK_SCREEN
                                )
                            }
                            TypeElementValidator.CLASS_ANDROIDX_FRAGMENT -> {
                                extensionFunSpecBuilder.addStatement(
                                    ".let { activity?.let { activity -> %T.%L(activity, it) } }",
                                    CLASS_COLLAR,
                                    FUNCTION_NAME_TRACK_SCREEN
                                )
                            }
                            TypeElementValidator.CLASS_ACTIVITY -> {
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
                }
                .forEach { addFunction(it) }
        }.build()
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