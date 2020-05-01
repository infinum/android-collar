package co.infinum.processor

import co.infinum.processor.collectors.AnalyticsEventsCollector
import co.infinum.processor.collectors.ScreenNamesCollector
import co.infinum.processor.collectors.UserPropertiesCollector
import co.infinum.processor.configurations.AnalyticsEventsConfiguration
import co.infinum.processor.configurations.ScreenNamesConfiguration
import co.infinum.processor.configurations.UserPropertiesConfiguration
import co.infinum.processor.extensions.consume
import co.infinum.processor.options.AnalyticsEventsOptions
import co.infinum.processor.options.ScreenNamesOptions
import co.infinum.processor.options.UserPropertiesOptions
import co.infinum.processor.subprocessors.AnalyticsEventsSubprocessor
import co.infinum.processor.subprocessors.ScreenNamesSubprocessor
import co.infinum.processor.subprocessors.UserPropertiesSubprocessor
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
internal class CollarProcessor : AbstractProcessor() {

    private val screenNamesSubprocessor: ScreenNamesSubprocessor = ScreenNamesSubprocessor()
    private val analyticsEventsSubprocessor: AnalyticsEventsSubprocessor = AnalyticsEventsSubprocessor()
    private val userPropertiesSubprocessor: UserPropertiesSubprocessor = UserPropertiesSubprocessor()

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        setOf(
            ScreenNamesCollector.SUPPORTED,
            AnalyticsEventsCollector.SUPPORTED,
            UserPropertiesCollector.SUPPORTED
        )
            .flatten()
            .toMutableSet()

    override fun getSupportedSourceVersion(): SourceVersion =
        SourceVersion.latestSupported()

    override fun getSupportedOptions(): Set<String> =
        setOf(
            ScreenNamesOptions.SUPPORTED,
            AnalyticsEventsOptions.SUPPORTED,
            UserPropertiesOptions.SUPPORTED
        )
            .flatten()
            .toSet()

    override fun init(processingEnv: ProcessingEnvironment): Unit =
        super.init(processingEnv).run {
            screenNamesSubprocessor.init(ScreenNamesConfiguration(processingEnv))
            analyticsEventsSubprocessor.init(AnalyticsEventsConfiguration(processingEnv))
            userPropertiesSubprocessor.init(UserPropertiesConfiguration(processingEnv))
        }

    @KotlinPoetMetadataPreview
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean =
        consume {
            screenNamesSubprocessor.process(roundEnv)
            analyticsEventsSubprocessor.process(roundEnv)
            userPropertiesSubprocessor.process(roundEnv)
        }
}
