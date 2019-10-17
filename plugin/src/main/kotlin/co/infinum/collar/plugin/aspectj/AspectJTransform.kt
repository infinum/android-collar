package co.infinum.collar.plugin.aspectj

import co.infinum.collar.plugin.config.Config
import co.infinum.collar.plugin.extensions.append
import co.infinum.collar.plugin.extensions.appendAll
import co.infinum.collar.plugin.logger.logAugmentationFinish
import co.infinum.collar.plugin.logger.logAugmentationStart
import co.infinum.collar.plugin.logger.logJarAspectAdded
import co.infinum.collar.plugin.logger.logNoAugmentation
import co.infinum.collar.plugin.utils.javaTask
import co.infinum.collar.plugin.utils.variantDataList
import com.android.build.api.transform.Context
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformInvocationBuilder
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.android.build.gradle.internal.variant.BaseVariantData
import com.android.utils.FileUtils
import com.google.common.collect.Sets
import org.aspectj.util.FileUtil
import org.gradle.api.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class AspectJTransform(
    private val project: Project,
    private val config: Config
) : Transform() {

    companion object {
        private const val TRANSFORM_NAME = "collar"
    }

    private val aspectJWeaver: AspectJWeaver = AspectJWeaver(project)

    init {
        project.afterEvaluate {
            variantDataList(config.plugin).forEach(::setupVariant)

            with(config.collar()) {
                aspectJWeaver.weaveInfo = weaveInfo
                aspectJWeaver.debugInfo = debugInfo
                aspectJWeaver.ignoreErrors = ignoreErrors
                aspectJWeaver.transformLogFile = transformLogFile
                aspectJWeaver.breakOnError = breakOnError
                aspectJWeaver.ajcArgs appendAll ajcArgs
            }
        }
    }

    override fun getName(): String {
        return TRANSFORM_NAME
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return Sets.immutableEnumSet(QualifiedContent.DefaultContentType.CLASSES)
    }

    override fun getOutputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT)
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    @Suppress("OverridingDeprecatedMember")
    override fun transform(context: Context, inputs: Collection<TransformInput>, referencedInputs: Collection<TransformInput>, outputProvider: TransformOutputProvider, isIncremental: Boolean) {
        this.transform(TransformInvocationBuilder(context)
            .addInputs(inputs)
            .addReferencedInputs(referencedInputs)
            .addOutputProvider(outputProvider)
            .setIncrementalMode(isIncremental).build())
    }

    override fun transform(transformInvocation: TransformInvocation) {
        val outputProvider = transformInvocation.outputProvider

        if (!transformInvocation.isIncremental) {
            outputProvider.deleteAll()
        }

        val outputDir = outputProvider.getContentLocation(TRANSFORM_NAME, outputTypes, scopes, Format.DIRECTORY)
        if (outputDir.isDirectory) FileUtils.deleteDirectoryContents(outputDir)
        FileUtils.mkdirs(outputDir)

        aspectJWeaver.destinationDir = outputDir.absolutePath
        aspectJWeaver.bootClasspath = config.getBootClasspath().joinToString(separator = File.pathSeparator)

        aspectJWeaver.inPath.clear()
        aspectJWeaver.aspectPath.clear()

        logAugmentationStart()

        val inputs = transformInvocation.referencedInputs
        inputs.forEach proceedInputs@{ input ->
            if (input.directoryInputs.isEmpty() && input.jarInputs.isEmpty())
                return@proceedInputs //if no inputs so nothing to proceed

            input.directoryInputs.forEach { dir ->
                aspectJWeaver.inPath append dir.file
                aspectJWeaver.classPath append dir.file
            }
            input.jarInputs.forEach { jar ->
                if (jar.file.absolutePath.contains("collar-core")) {
//                    aspectJWeaver.aspectPath append jar.file
                } else {
//                    aspectJWeaver.classPath append jar.file
//                    aspectJWeaver.inPath append jar.file
                }
            }
        }

        val classpathFiles = aspectJWeaver.classPath.filter { it.isDirectory && it.list().isNotEmpty() }
        val inpathFiles = aspectJWeaver.inPath.filter { it.isDirectory && it.list().isNotEmpty() }
        if (inpathFiles.isEmpty() || classpathFiles.isEmpty()) {
            logNoAugmentation()
            return
        }

        aspectJWeaver.inPath append outputDir
        aspectJWeaver.doWeave()

        copyUnprocessedFiles(inputs, outputDir)

        logAugmentationFinish()
    }

    private fun <T : BaseVariantData> setupVariant(variantData: T) {
        val javaTask = javaTask(variantData)
        aspectJWeaver.encoding = javaTask.options.encoding
        aspectJWeaver.sourceCompatibility = config.collar().java.toString()
        aspectJWeaver.targetCompatibility = config.collar().java.toString()
    }

    private fun copyUnprocessedFiles(inputs: Collection<TransformInput>, outputDir: File) {
        inputs.forEach { input ->
            input.directoryInputs.forEach { dir ->
                copyUnprocessedFiles(dir.file.toPath(), outputDir.toPath())
            }
        }
    }

    private fun copyUnprocessedFiles(inDir: Path, outDir: Path) {
        Files.walk(inDir).forEach traverse@{ inFile ->
            val outFile = outDir.resolve(inDir.relativize(inFile))

            if (Files.exists(outFile))
                return@traverse

            if (Files.isDirectory(outFile)) {
                Files.createDirectory(outFile)
            } else {
                Files.copy(inFile, outFile)
            }
        }
    }
}
