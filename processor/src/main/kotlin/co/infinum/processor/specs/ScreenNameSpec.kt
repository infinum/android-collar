package co.infinum.processor.specs

import co.infinum.processor.extensions.applyIf
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
    private val holders: Set<ScreenHolder>
) : CommonSpec(outputDir, DEFAULT_PACKAGE_NAME, DEFAULT_SIMPLE_NAME) {

    companion object {
        private const val DEFAULT_SIMPLE_NAME = "ScreenNames"
        private const val FUNCTION_NAME_TRACK_SCREEN = "trackScreen"
    }

    open class Builder(
        private var outputDir: File? = null,
        private var holders: Set<ScreenHolder> = setOf()
    ) {
        fun outputDir(outputDir: File) = apply { this.outputDir = outputDir }
        fun holders(holders: Set<ScreenHolder>) = apply { this.holders = holders }
        fun build() = ScreenNameSpec(outputDir!!, holders)
    }

    init {
        build()
    }

    override fun file(): FileSpec =
        super.file().toBuilder(DEFAULT_PACKAGE_NAME, DEFAULT_SIMPLE_NAME)
            .applyIf(hasDeprecatedClasses()) { addAnnotation(suppressDeprecation()) }
            .build()

    override fun extensions(): List<FunSpec> =
        holders
            .groupBy { it.superClassName }
            .keys
            .filterNotNull()
            .map { it to holders.groupBy { holder -> holder.superClassName }[it].orEmpty() }
            .toMap()
            .map { mapEntry ->
                FunSpec.builder(FUNCTION_NAME_TRACK_SCREEN)
                    .receiver(mapEntry.key)
                    .applyIf(mapEntry.value.isNotEmpty()) {
                        beginControlFlow("when (this)")
                        addCode(
                            CodeBlock.builder()
                                .apply {
                                    mapEntry.value.forEach {
                                        when (mapEntry.key) {
                                            CLASS_COMPONENT_ACTIVITY -> addActivityStatement(this, it)
                                            CLASS_ACTIVITY -> addActivityStatement(this, it)
                                            CLASS_ANDROIDX_FRAGMENT -> addFragmentStatement(this, it)
                                            else -> addFragmentStatement(this, it)
                                        }
                                    }
                                }
                                .build()
                        )
                        endControlFlow()
                    }
                    .build()
            }

    private fun hasDeprecatedClasses(): Boolean =
        holders
            .groupBy { it.superClassName }
            .map { it.key }
            .filterNotNull()
            .any { it == CLASS_SUPPORT_FRAGMENT || it == CLASS_FRAGMENT }

    private fun suppressDeprecation(): AnnotationSpec =
        AnnotationSpec.builder(Suppress::class.java)
            .addMember(CodeBlock.of("%S", "DEPRECATION"))
            .build()

    private fun addActivityStatement(builder: CodeBlock.Builder, holder: ScreenHolder) =
        builder.addStatement(
            "is %T -> %T.%L(this, %S)",
            holder.className,
            CLASS_COLLAR,
            FUNCTION_NAME_TRACK_SCREEN,
            holder.screenName
        )

    private fun addFragmentStatement(builder: CodeBlock.Builder, holder: ScreenHolder) =
        builder.addStatement(
            "is %T -> activity?.let { %T.%L(it, %S) }",
            holder.className,
            CLASS_COLLAR,
            FUNCTION_NAME_TRACK_SCREEN,
            holder.screenName
        )
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