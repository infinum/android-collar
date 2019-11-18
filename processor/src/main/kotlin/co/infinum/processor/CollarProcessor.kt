package co.infinum.processor

import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.annotations.AnalyticsEvent
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(CollarProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
//@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
class CollarProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

//      @Override public Set<String> getSupportedOptions() {
//    ImmutableSet.Builder<String> builder = ImmutableSet.builder();
//    builder.add(OPTION_SDK_INT, OPTION_DEBUGGABLE);
//    if (trees != null) {
//      builder.add(IncrementalAnnotationProcessorType.ISOLATING.getProcessorOption());
//    }
//    return builder.build();
//  }

//    override fun getSupportedOptions(): MutableSet<String> {
//        return mutableSetOf(
//            IncrementalAnnotationProcessorType.ISOLATING.getProcessorOption()
//        )
//    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(
            AnalyticsEvent::class.java.name,
            ScreenName::class.java.name
        )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        roundEnvironment?.getElementsAnnotatedWith(AnalyticsEvent::class.java)?.forEach { element ->
            generateExtension(
                processingEnv.elementUtils.getPackageOf(element).toString(),
                element.enclosingElement.simpleName.toString(),
//                element.simpleName.toString()
            element.getAnnotation(AnalyticsEvent::class.java)
            )
        }

        return true
    }

    private fun generateExtension(packageName: String, className: String, value: String) {
        val fileName = "Collar_${className}_$value"

        val square = FunSpec.builder("collar${className.capitalize()}${value.capitalize()}")
            .returns(Unit::class)
            .addStatement("Collar.trackEvent(%S)", value)
            .build()

        val file = FileSpec.builder(packageName, fileName)
            .addImport("co.infinum.collar", "Collar")
            .addFunction(square)
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
    }
}