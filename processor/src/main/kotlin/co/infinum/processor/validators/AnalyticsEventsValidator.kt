package co.infinum.processor.validators

import co.infinum.processor.extensions.isSealedClass
import co.infinum.processor.extensions.showError
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.AnalyticsEventsHolder
import co.infinum.processor.options.Options
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.Messager
import javax.lang.model.util.Types

class AnalyticsEventsValidator(
    private val processorOptions: Options,
    private val typeUtils: Types,
    private val messager: Messager
) : Validator<AnalyticsEventsHolder> {

    @KotlinPoetMetadataPreview
    override fun validate(elements: Set<AnalyticsEventsHolder>): Set<AnalyticsEventsHolder> {
        val validRootClasses = elements.filter {
            if (it.rootClass.isSealedClass().not()) {
                messager.showWarning("$it is not a sealed Kotlin class.")
                false
            } else {
                true
            }
        }
        val innerClassesCount = validRootClasses.flatMap { it.eventHolders }.count()
        return if (innerClassesCount > processorOptions.maxCount()) {
            messager.showError("You can report up to ${processorOptions.maxCount()} different events per app. Current size is ${innerClassesCount}.")
            setOf()
        } else {
            validRootClasses.map { rootClass ->
                rootClass.copy(eventHolders = rootClass.eventHolders.filter { holder ->
                    if (holder.enabled) {
                        if (typeUtils.directSupertypes(holder.type).contains(rootClass.rootClass.asType())) {
                            if (holder.eventName.length > processorOptions.maxNameSize()) {
                                messager.showWarning("Event names can be up to ${processorOptions.maxNameSize()} characters long. ${holder.eventName} is ${holder.eventName.length} long.")
                                false
                            } else {
                                if (holder.eventName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                                    messager.showWarning("Event name may only contain alphanumeric characters and underscores (\"_\"). ${holder.eventName} does not.")
                                    false
                                } else {
                                    if (holder.eventName.first().isLetter().not()) {
                                        messager.showWarning("Event name must start with an alphabetic character. ${holder.eventName.first()} in ${holder.eventName} is not a letter.")
                                        false
                                    } else {
                                        if (processorOptions.reservedPrefixes().any { holder.eventName.startsWith(it, false) } || processorOptions.reserved().any { holder.eventName.equals(it, false) }) {
                                            messager.showWarning("The ${processorOptions.reservedPrefixes().plus(processorOptions.reserved()).joinToString { "\"$it\"" }} event names are reserved and cannot be used as in ${holder.eventName}.")
                                            false
                                        } else {
                                            if (holder.eventParameters.size > processorOptions.maxParametersCount()) {
                                                messager.showWarning("You can associate up to ${processorOptions.maxParametersCount()} unique parameter with each user property. Current size is ${holder.eventParameters.size}.")
                                                false
                                            } else {
                                                true
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            messager.showWarning("$holder does not extend from $rootClass.")
                            false
                        }
                    } else {
                        false
                    }
                }.toSet()
                )
            }.toSet()
        }
    }
}