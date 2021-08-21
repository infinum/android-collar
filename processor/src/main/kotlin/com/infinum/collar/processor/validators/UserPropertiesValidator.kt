package com.infinum.collar.processor.validators

import com.infinum.collar.processor.extensions.isSealedClass
import com.infinum.collar.processor.extensions.showError
import com.infinum.collar.processor.extensions.showWarning
import com.infinum.collar.processor.models.PropertyHolder
import com.infinum.collar.processor.models.UserPropertiesHolder
import com.infinum.collar.processor.options.Options
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.Messager
import javax.lang.model.util.Types

internal class UserPropertiesValidator(
    private val processorOptions: Options,
    private val typeUtils: Types,
    private val messager: Messager
) : Validator<UserPropertiesHolder> {

    @KotlinPoetMetadataPreview
    override fun validate(elements: Set<UserPropertiesHolder>): Set<UserPropertiesHolder> {
        val validRootClasses = elements.filter {
            if (it.rootClass.isSealedClass().not()) {
                messager.showWarning("$it is not a sealed Kotlin class.")
                false
            } else {
                true
            }
        }
        val innerClassesCount = validRootClasses.flatMap { it.propertyHolders }.count()
        return if (innerClassesCount > processorOptions.maxCount()) {
            messager.showError(
                "You can report up to ${processorOptions.maxCount()} different user properties per app. " +
                    "Current size is $innerClassesCount."
            )
            setOf()
        } else {
            validRootClasses.map { rootClass ->
                rootClass.copy(
                    propertyHolders = rootClass.propertyHolders
                        .filter { it.enabled && validateSuperType(it, rootClass) }
                        .toSet()
                )
            }.toSet()
        }
    }

    private fun validateSuperType(holder: PropertyHolder, rootClass: UserPropertiesHolder): Boolean =
        if (typeUtils.directSupertypes(holder.type).contains(rootClass.rootClass.asType())) {
            validateNameLength(holder)
        } else {
            messager.showWarning("Property $holder does not extend from $rootClass.")
            false
        }

    private fun validateNameLength(holder: PropertyHolder): Boolean =
        if (holder.propertyName.length > processorOptions.maxNameSize()) {
            messager.showWarning(
                "User property name can be up to ${processorOptions.maxNameSize()} characters long. " +
                    "${holder.propertyName} is ${holder.propertyName.length} long."
            )
            false
        } else {
            validateName(holder)
        }

    private fun validateName(holder: PropertyHolder): Boolean =
        if (holder.propertyName.matches(Regex("^[a-zA-Z0-9_]*$"))) {
            validateNameStart(holder)
        } else {
            messager.showWarning(
                "Property name may only contain alphanumeric characters and underscores (\"_\"). " +
                    "${holder.propertyName} does not."
            )
            false
        }

    private fun validateNameStart(holder: PropertyHolder): Boolean =
        if (holder.propertyName.first().isLetter()) {
            validateReserved(holder)
        } else {
            messager.showWarning(
                "User property name must start with an alphabetic character. " +
                    "${holder.propertyName.first()} in ${holder.propertyName} is not a letter."
            )
            false
        }

    private fun validateReserved(holder: PropertyHolder): Boolean =
        if (
            processorOptions
                .reservedPrefixes()
                .any { holder.propertyName.startsWith(it, false) }.not() && processorOptions.reserved()
                .any { holder.propertyName.equals(it, false) }.not()
        ) {
            validateExistingParameters(holder)
        } else {
            messager.showWarning(
                "The ${
                processorOptions
                    .reservedPrefixes()
                    .plus(processorOptions.reserved())
                    .joinToString { "\"$it\"" }
                }" +
                    " user properties are reserved and cannot be used as in ${holder.propertyName}."
            )
            false
        }

    private fun validateExistingParameters(holder: PropertyHolder): Boolean =
        if (holder.propertyParameterNames.isNotEmpty()) {
            validateParameterCount(holder)
        } else {
            messager.showWarning(
                "You must associate at least ${processorOptions.maxParametersCount()} unique parameter. " +
                    "${holder.propertyName} has none."
            )
            false
        }

    private fun validateParameterCount(holder: PropertyHolder): Boolean =
        if (holder.propertyParameterNames.size > processorOptions.maxParametersCount()) {
            messager.showWarning(
                "You can associate up to ${processorOptions.maxParametersCount()} " +
                    "unique parameter with each user property. " +
                    "Current size is ${holder.propertyParameterNames.size}."
            )
            false
        } else {
            true
        }
}
