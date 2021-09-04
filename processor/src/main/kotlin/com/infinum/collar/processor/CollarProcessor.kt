package com.infinum.collar.processor

import com.infinum.collar.processor.collectors.AnalyticsEventsCollector
import com.infinum.collar.processor.collectors.ScreenNamesCollector
import com.infinum.collar.processor.collectors.UserPropertiesCollector
import com.infinum.collar.processor.configurations.AnalyticsEventsConfiguration
import com.infinum.collar.processor.configurations.ScreenNamesConfiguration
import com.infinum.collar.processor.configurations.UserPropertiesConfiguration
import com.infinum.collar.processor.extensions.consume
import com.infinum.collar.processor.options.AnalyticsEventsOptions
import com.infinum.collar.processor.options.ScreenNamesOptions
import com.infinum.collar.processor.options.UserPropertiesOptions
import com.infinum.collar.processor.subprocessors.AnalyticsEventsSubprocessor
import com.infinum.collar.processor.subprocessors.ScreenNamesSubprocessor
import com.infinum.collar.processor.subprocessors.UserPropertiesSubprocessor
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

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

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean =
        consume {
            screenNamesSubprocessor.process(roundEnv)
            analyticsEventsSubprocessor.process(roundEnv)
            userPropertiesSubprocessor.process(roundEnv)
        }
}
