package co.infinum.collar.processor.subprocessors

import co.infinum.collar.processor.collectors.UserPropertiesCollector
import co.infinum.collar.processor.extensions.asClassName
import co.infinum.collar.processor.extensions.showError
import co.infinum.collar.processor.specs.UserPropertiesSpec
import co.infinum.collar.processor.validators.UserPropertiesValidator
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment

internal class UserPropertiesSubprocessor : CommonSubprocessor() {

    @KotlinPoetMetadataPreview
    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = UserPropertiesCollector(roundEnvironment)
        val validator = UserPropertiesValidator(processorOptions, typeUtils, messager)

        collector.collect().run {
            validator.validate(this).forEach {
                generatedDir?.let { outputDir ->
                    UserPropertiesSpec(outputDir, it.rootClass.asClassName(), it.propertyHolders)()
                } ?: run {
                    messager.showError("Cannot find generated output dir.")
                }
            }
        }
    }
}
