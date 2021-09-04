package com.infinum.collar.processor.validators

import com.infinum.collar.processor.extensions.isSealedClass
import com.infinum.collar.processor.extensions.showError
import com.infinum.collar.processor.extensions.showWarning
import com.infinum.collar.processor.models.AnalyticsEventsHolder
import com.infinum.collar.processor.models.EventHolder
import com.infinum.collar.processor.options.Options
import javax.annotation.processing.Messager
import javax.lang.model.util.Types

internal class AnalyticsEventsValidator(
    private val processorOptions: Options,
    private val typeUtils: Types,
    private val messager: Messager
) : Validator<AnalyticsEventsHolder> {

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
            messager.showError(
                "You can report up to ${processorOptions.maxCount()} different events per app. " +
                    "Current size is $innerClassesCount."
            )
            setOf()
        } else {
            validRootClasses.map { rootClass ->
                rootClass.copy(
                    eventHolders = rootClass.eventHolders
                        .filter { it.enabled && validateSuperType(it, rootClass) }
                        .toSet()
                )
            }.toSet()
        }
    }

    private fun validateSuperType(holder: EventHolder, rootClass: AnalyticsEventsHolder): Boolean =
        if (typeUtils.directSupertypes(holder.type).contains(rootClass.rootClass.asType())) {
            validateNameLength(holder)
        } else {
            messager.showWarning("Event $holder does not extend from $rootClass.")
            false
        }

    private fun validateNameLength(holder: EventHolder): Boolean =
        if (holder.eventName.length > processorOptions.maxNameSize()) {
            messager.showWarning(
                "Event names can be up to ${processorOptions.maxNameSize()} characters long. " +
                    "${holder.eventName} is ${holder.eventName.length} long."
            )
            false
        } else {
            validateName(holder)
        }

    private fun validateName(holder: EventHolder): Boolean =
        if (holder.eventName.matches(Regex("^[a-zA-Z0-9_]*$"))) {
            validateNameStart(holder)
        } else {
            messager.showWarning(
                "Event name may only contain alphanumeric characters and underscores (\"_\"). " +
                    "${holder.eventName} does not."
            )
            false
        }

    private fun validateNameStart(holder: EventHolder): Boolean =
        if (holder.eventName.first().isLetter()) {
            validateReserved(holder)
        } else {
            messager.showWarning(
                "Event name must start with an alphabetic character. " +
                    "${holder.eventName.first()} in ${holder.eventName} is not a letter."
            )
            false
        }

    private fun validateReserved(holder: EventHolder): Boolean =
        if (
            processorOptions
                .reservedPrefixes()
                .any { holder.eventName.startsWith(it, false) }.not() && processorOptions.reserved()
                .any { holder.eventName.equals(it, false) }.not()
        ) {
            validateParameterCount(holder)
        } else {
            messager.showWarning(
                "The ${
                processorOptions
                    .reservedPrefixes()
                    .plus(processorOptions.reserved())
                    .joinToString { "\"$it\"" }
                }" +
                    " event names are reserved and cannot be used as in ${holder.eventName}."
            )
            false
        }

    private fun validateParameterCount(holder: EventHolder): Boolean =
        if (holder.eventParameters.size > processorOptions.maxParametersCount()) {
            messager.showWarning(
                "You can associate up to ${processorOptions.maxParametersCount()} unique parameter with each event. " +
                    "Current size is ${holder.eventParameters.size}."
            )
            false
        } else {
            validateParametersType(holder)
        }

    private fun validateParametersType(holder: EventHolder): Boolean {
        val invalidParameterTypes = holder.eventParameters.filter { it.method.isBlank() }
        return if (invalidParameterTypes.isNotEmpty()) {
            messager.showWarning(
                "Event parameters ${invalidParameterTypes.joinToString(", ") { it.variableName }}" +
                    " from event ${holder.className.simpleName} are not supported."
            )
            false
        } else {
            true
        }
    }
}
