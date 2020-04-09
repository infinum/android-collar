package co.infinum.processor.subprocessors

import co.infinum.processor.collectors.UserPropertiesCollector
import co.infinum.processor.extensions.showError
import co.infinum.processor.specs.userPropertiesSpec
import co.infinum.processor.validators.UserPropertiesValidator
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.RoundEnvironment

class UserPropertiesSubprocessor : CommonSubprocessor() {

    @KotlinPoetMetadataPreview
    override fun process(roundEnvironment: RoundEnvironment) {
        val collector = UserPropertiesCollector(roundEnvironment)
        val validator = UserPropertiesValidator(processorOptions, typeUtils, messager)

        collector.collect().run {
            validator.validate(this).forEach {
                generatedDir?.let { outputDir ->
                    userPropertiesSpec {
                        outputDir(outputDir)
                        className(it.rootClass.asClassName())
                        holders(it.propertyHolders)
                    }
                } ?: run {
                    messager.showError("Cannot find generated output dir.")
                }
            }
        }
    }
}